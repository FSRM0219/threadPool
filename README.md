    /**
     * 修改线程池配置
     * curl --request POST \
     * --url http://localhost:8089/api/v1/dynamic/thread/pool/update_thread_pool_config \
     * --header 'content-type: application/json' \
     * --data '{
     * "appName":"dynamic-thread-pool-test-app",
     * "threadPoolName": "threadPoolExecutor",
     * "corePoolSize": 1,
     * "maximumPoolSize": 10
     * }'
     */
#####
    /**
    * 查询线程池配置
    * curl --request GET \
    * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_config?appName=dynamic-thread-pool-test-app&threadPoolName=threadPoolExecutor'
    */
#####
    /**
    * 查询线程池数据
    * curl --request GET \
    * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_list'
    */
