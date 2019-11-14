# stt-parent
所有项目依赖的父项目，管理jar包，提供公共类和通用工具类

# 使用
在其他项目pom.xml如下引用
  
    <parent>
        <groupId>cn.stt</groupId>
        <artifactId>stt-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>cn.stt</groupId>
            <artifactId>stt-common</artifactId>
            <version>${project.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>*</groupId>
                    <artifactId>*</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
