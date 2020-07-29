/**
 * 
 */
package com.amp.data.handler.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import com.amp.common.api.config.bean.PersistenceConfig;
import com.amp.common.api.config.bean.QueriesConfig;
import com.amp.common.api.config.bean.ToolkitSettingsConfig;
import com.amp.common.api.impl.ToolkitDataProvider;
import com.amp.common.api.impl.ToolkitDatabase;
import com.amp.common.api.impl.ToolkitJAXB;
import com.amp.common.api.impl.ToolkitSQL;
import com.amp.common.api.settings.ToolkitSettingsSpring;
import com.amp.data.handler.aws.DataHandlerAWS;

/**
 * @author MVEKSLER
 *
 */
@Configuration
public class DataHandlerBeansConfig
{
	@Autowired
	ApplicationContext applicationContext;
	
	/**
	 * 
	 */
	public DataHandlerBeansConfig() {
		
	}

	@Primary
	@Bean("dataHandlerAWS")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DataHandlerAWS dataHandlerAWS() 
	{
	      return new DataHandlerAWS();
	}
	
	@Primary
	@Bean("toolkitDataProvider")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ToolkitDataProvider toolkitDataProvider() 
	{
	      return new ToolkitDataProvider();
	}
	
	@Primary
	@Bean("toolkitSettingsConfig") 
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ToolkitSettingsConfig toolkitSettingsConfig() 
	{
	      return new ToolkitSettingsConfig();
	}
	
	@Primary
	@Bean("persistenceConfig")  
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PersistenceConfig persistenceConfig() 
	{
	      return new PersistenceConfig();
	}
	
	@Primary
	@Bean("queriesConfig") 
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public QueriesConfig queriesConfig() 
	{
	      return new QueriesConfig();
	}
	
	@Primary
	@Bean("toolkitSettingsSpring") 
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ToolkitSettingsSpring toolkitSettingsSpring() 
	{
	      return new ToolkitSettingsSpring();
	}
	
	@Primary
	@Bean("toolkitJAXB") 
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ToolkitJAXB toolkitJAXB() 
	{
	      return new ToolkitJAXB();
	}
	
	@Primary
	@Bean("toolkitSQL")  
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ToolkitSQL toolkitSQL() 
	{
	      return new ToolkitSQL();
	}
	
	@Primary
	@Bean("toolkitDatabase") 
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ToolkitDatabase toolkitDatabase() 
	{
	      return new ToolkitDatabase();
	}
}

