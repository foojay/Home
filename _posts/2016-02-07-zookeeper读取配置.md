---
layout: post
title:  "读取配置"
date:   2016-02-07 11:27:00
categories: zookeeper
excerpt:  读取配置
---

* content
{:toc}


### applicationContext.xml

       <bean id="zookeeperClientUtil" lazy-init="false"
            class="com.SimulationInfo.service.basic.ZookeeperClientUtil" init-method="init">
            <property name="servers" value="172.16.1.203:2182" />
            <property name="nameSpace" value="SimulationInfo-250" />
            <property name="isZookeeper" value="true" />
        </bean>
        <bean id="zooKeeperPropertyPlaceholderConfigurer"
            class="com.SimulationInfo.service.basic.ZooKeeperPropertyPlaceholderConfigurer">
            <property name="configFile" value="/config" />
            <property name="isZookeeper" value="true" />
            <property name="location" value="classpath:base.properties" />
            <property name="configurationClient" ref="zookeeperClientUtil"></property>
            <property name="order" value="1" />
            <property name="ignoreUnresolvablePlaceholders" value="true" />
        </bean>


### ZookeeperClientUtil

        public class ZookeeperClientUtil {
            private Logger logger = Logger.getLogger(ZookeeperClientUtil.class);
            // 初始化zookeeper
            private static CuratorFramework zkclient = null;
            // zookeeper地址
            private String servers;
            private String nameSpace;
            private String isZookeeper;
            public String getIsZookeeper() {
                return isZookeeper;
            }
            public void setIsZookeeper(String isZookeeper) {
                this.isZookeeper = isZookeeper;
            }
            public void setServers(String servers) {
                this.servers = servers;
            }
            public void setNameSpace(String nameSpace) {
                this.nameSpace = nameSpace;
            }
            @SuppressWarnings("deprecation")
            public void init() {
                // 启用zookeeper
                if (StringUtils.equals("true", isZookeeper)) {
                    RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);// 重试机制
                    Builder builder = CuratorFrameworkFactory.builder()
                            .connectString(servers).connectionTimeoutMs(5000)
                            .sessionTimeoutMs(5000).retryPolicy(rp);
                    builder.namespace(nameSpace);
                    CuratorFramework zclient = builder.build();
                    zkclient = zclient;
                    zkclient.start();// 放在这前面执行
                }
            }
            /**
             * 创建或更新一个节点
             * 
             * @param path
             *            路径
             * @param content
             *            内容
             * **/
            @SuppressWarnings("deprecation")
            public void createrOrUpdate(String path, String content) throws Exception {
         
                zkclient.newNamespaceAwareEnsurePath(path).ensure(
                        zkclient.getZookeeperClient());
                zkclient.setData().forPath(path, content.getBytes());
                System.out.println("添加成功！！！");
            }
            /**
             * 删除zk节点
             * 
             * @param path
             *            删除节点的路径
             * 
             * **/
            public void delete(String path) throws Exception {
                zkclient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
                logger.info("删除成功!" + path);
            }
            /**
             * 判断路径是否存在
             * 
             * @param path
             * **/
            public void checkExist(String path) throws Exception {
                if (zkclient.checkExists().forPath(path) == null) {
                    logger.info("路径不存在!" + path);
                } else {
                    logger.info("路径已经存在!" + path);
                }
            }
            /**
             * 读取的路径
             * 
             * @param path
             * **/
            public String read(String path) throws Exception {
                String data = new String(zkclient.getData().forPath(path), "utf-8");
                logger.info("读取的数据:" + data);
                return data;
            }
            /**
             * @param path
             *            路径 获取某个节点下的所有子文件
             * */
            public void getListChildren(String path) throws Exception {
                List<String> paths = zkclient.getChildren().forPath(path);
                for (String p : paths) {
                    logger.info(p);
                }
            }
            /**
             * @param zkPath
             *            zk上的路径
             * @param localpath
             *            本地上的文件路径
             * 
             * **/
            public void upload(String zkPath, String localpath) throws Exception {
                createrOrUpdate(zkPath, "");// 创建路径
                byte[] bs = FileUtils.readFromFile(localpath);
                zkclient.setData().forPath(zkPath, bs);
                logger.info("上传文件成功！");
            }
            public static InputStream getStringInputStream(String s) {
                if (s != null && !s.equals("")) {
                    try {
                        ByteArrayInputStream stringInputStream = new ByteArrayInputStream(
                                s.getBytes());
                        return stringInputStream;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
            
            
        }

### FileUtils.readFromFile方法

    /**
         * 
         * @Title: readFromFile
         * @Description: 读取文件，转换为byte[]
         * @param file
         * @return byte[]
         */
        public static byte[] readFromFile(File file) {
            InputStream is = null;
            byte[] ret = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
                ret = readFromStream(is);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            return ret;
        }




### ZooKeeperPropertyPlaceholderConfigurer

    public class ZooKeeperPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
        private Logger logger = Logger.getLogger(ZooKeeperPropertyPlaceholderConfigurer.class);
        private String configFile;
        private String isZookeeper;
        private ZookeeperClientUtil configurationClient;
        public void setIsZookeeper(String isZookeeper) {
            this.isZookeeper = isZookeeper;
        }
        public void setConfigFile(String configFile) {
            this.configFile = configFile;
        }
        public void setConfigurationClient(ZookeeperClientUtil configurationClient) {
            this.configurationClient = configurationClient;
        }
        @Override
        protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
            // 读取配置文件
            try {
                // 启用zookeeper
                if (StringUtils.equals("true", isZookeeper)) {
                    // 从zookeeper同步属性
                    String data = configurationClient.read(configFile);
                    if (StringUtils.isNotBlank(data)) {
                        props = loadProperties(data);
                    } else {
                        logger.error("zookeeper获取配置文件失败;" + configFile);
                    }
                }
                
                // 初始化应用配置属性
                Config.init(props);
                logger.info(Constants.UPLOAD_BASE_FOLD);
                // 从配置文件读取
                super.processProperties(beanFactoryToProcess, props);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /***
         * 
         * loadProperties:加载数据. <br/>
         *
         * @author xieyun
         * @param data
         * @since JDK 1.6
         */
        private Properties loadProperties(String data) {
            ByteArrayInputStream stringInputStream = null;
            try {
                stringInputStream = new ByteArrayInputStream(data.getBytes());
                Properties prop = new Properties();// 属性集合对象
                prop.load(stringInputStream);// 将属性文件流装载到Properties对象中
                return prop;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stringInputStream != null) {
                    try {
                        stringInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        }
    }