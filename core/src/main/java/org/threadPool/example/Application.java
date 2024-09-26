package org.threadPool.example;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Configurable
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    /*
     * ApplicationRunner是SpringBoot提供的回调接口
     * 使用SpringApplication.run(Application.class, args)启动SpringBoot应用时，
     * 首先初始化应用上下文，扫描其中的所有Bean，并寻找实现ApplicationRunner接口的Bean，
     * 初始化完成后，Spring自动调用所有ApplicationRunner Bean的run方法，同时传递启动时的命令行参数
     * */
    @Bean
    public ApplicationRunner applicationRunner(ExecutorService threadPoolExecutor01) {
        return args -> {
            while (true) {

                Random random = new Random();

                int startTime = random.nextInt(10) + 1; // 1到10秒之间

                int workTime = random.nextInt(10) + 1; // 1到10秒之间

                threadPoolExecutor01.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(startTime);
                            System.out.println("Task started after " + startTime + " seconds.");

                            TimeUnit.SECONDS.sleep(workTime);
                            System.out.println("Task executed for " + workTime + " seconds.");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });

                Thread.sleep(random.nextInt(50) + 1);
            }
        };
    }
}
