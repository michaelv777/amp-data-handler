/**
 * 
 */
package com.amp.data.handler.factory;

import com.amp.data.handler.base.DataHandlerBase;

/**
 * @author MVEKSLER
 *
 */
public class DataHandlerFactory 
{

	/**
	 * 
	 */
	public DataHandlerFactory() 
	{
		// TODO Auto-generated constructor stub
	}

	public static DataHandlerBase getDataHandler(Class<?> clazz)
	{
		try
		{
			if ( null == clazz )
			{
				return null;
			}
			else
			{
				return (DataHandlerBase)clazz.newInstance();
			}
		}
		catch( InstantiationException ie )
		{
			return null;
		}
		catch( IllegalAccessException ile )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
	}
}
