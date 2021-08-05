<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<beans default-lazy-init="false" xmlns:p="http://www.springframework.org/schema/p" xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.2.xsd
		http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.2.xsd">

	<bean class="cn.sunline.common.spring.I18NResource">
		<property name="values">
			<list>
<#list defNames as name>
				<value>${name}</value>
</#list>
			</list>
		</property>
	</bean>
</beans>
