/**
 * 
 */
package com.amp.data.handler.base;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.amp.common.api.impl.ToolkitDataProvider;
import com.amp.common.api.impl.ToolkitReflection;
import com.amp.jpa.entities.Category;
import com.amp.jpa.entities.Node;

/**
 * @author MVEKSLER
 *
 */
public abstract class DataHandlerBase implements DataHandlerI {
	private static final Logger LOG = LoggerFactory.getLogger(DataHandlerBase.class);

	// ---class variables

	@Autowired
	protected ToolkitDataProvider cToolkitDataProvider = null;

	@Autowired
	protected ApplicationContext applicationContext = null;

	private ToolkitReflection iReflection = null;

	protected HashMap<String, String> cSystemConfiguration = 
			new HashMap<String, String>();

	protected HashMap<String, String> cWorkerConfiguration = 
			new HashMap<String, String>();

	protected boolean lcRes = true;

	// ---getters/setters

	/**
	 * @return the cDataProvider
	 */
	public ToolkitDataProvider getcToolkitDataProvider() {
		return cToolkitDataProvider;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public HashMap<String, String> getcSystemConfiguration() {
		return cSystemConfiguration;
	}

	public void setcSystemConfiguration(HashMap<String, String> cSystemConfiguration) {
		this.cSystemConfiguration = cSystemConfiguration;
	}

	public HashMap<String, String> getcWorkerConfiguration() {
		return cWorkerConfiguration;
	}

	public void setcWorkerConfiguration(HashMap<String, String> cWorkerConfiguration) {
		this.cWorkerConfiguration = cWorkerConfiguration;
	}

	

	/**
	 * @param cDataProvider the cDataProvider to set
	 */
	public void setcToolkitDataProvider(ToolkitDataProvider cDataProvider) {
		this.cToolkitDataProvider = cDataProvider;
	}

	/**
	 * @param lcRes the lcRes to set
	 */
	public void setLcRes(boolean lcRes) {
		this.lcRes = lcRes;
	}

	/**
	 * @return the lcRes
	 */
	public boolean isLcRes() {
		return lcRes;
	}

	// ---class methods
	/**
	 * 
	 */
	public DataHandlerBase() 
	{
		try
		{
			
		} 
		catch (Exception e)
		{
			LOG.error("MappingHandler:" + e.getStackTrace());

			this.setLcRes(false);
		}
	}

	public DataHandlerBase(HashMap<String, String> cSystemConfiguration, HashMap<String, String> cWorkerConfiguration) 
	{
		try
		{
			this.setcSystemConfiguration(cSystemConfiguration);

			this.setcWorkerConfiguration(cWorkerConfiguration);
		} 
		catch (Exception e)
		{
			LOG.error("MappingHandler:" + e.getStackTrace());

			this.setLcRes(false);
		}
	}

	// ---
	protected boolean init() 
	{
		boolean cRes = true;

		String methodName = "";

		try
		{
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			
			// -----------------
			if (cRes)
			{
				if ( null == this.cToolkitDataProvider )
    			{
					if ( this.applicationContext != null )
					{
						this.cToolkitDataProvider = (ToolkitDataProvider) this.applicationContext
								.getBean("toolkitDataProvider");
		
						if (null == this.cToolkitDataProvider) 
						{
							cRes = false;
		
							LOG.error(methodName + "::cToolkitDataProvider is NULL!");
						}
						else 
						{
							cRes = this.cToolkitDataProvider.isLcRes();
		
							LOG.info(methodName + "::cToolkitDataProvider status is " + cRes);
						}
					}
					else
					{
						LOG.error(methodName + "::cToolkitDataProvider and applicationContext are null!");
						
						cRes = false;
					}
    			}
			}
			// ---
			if (cRes)
			{
				List<Class<? extends Object>> clazzes = this.cToolkitDataProvider.gettDatabase()
						.getPersistanceClasses();

				this.cToolkitDataProvider.gettDatabase().getHibernateSession(clazzes);
			}
			// ---
			return cRes;
		} 
		catch (NoSuchBeanDefinitionException nbd)
		{
			LOG.error(methodName + "::" + nbd.getMessage());

			this.setLcRes(cRes = false);

			return cRes;
		} 
		catch (BeansException be) 
		{
			LOG.error(methodName + "::" + be.getMessage());

			this.setLcRes(cRes = false);

			return cRes;
		} 
		catch (Exception e) 
		{
			LOG.error(methodName + "::" + e.getMessage());

			this.setLcRes(cRes = false);

			return cRes;
		}
	}

	// ---
	/*
	 * protected boolean configureSpring() { boolean cRes = true;
	 * 
	 * try {
	 * 
	 * FileSystemResource beansDefinition = new
	 * FileSystemResource(this.getcFrameworkBeansConfig());
	 * 
	 * // create the Spring container this.cBeanFactory = new
	 * XmlBeanFactory(beansDefinition);
	 * 
	 * // create a property configurator, that updates properties in the beans
	 * definition file with values // from a properties file. It replaces ${property
	 * name} with the value from the propeties file. Resource[] locations = new
	 * Resource[] { new FileSystemResource(this.getcFrameworkBeansProps()) };
	 * 
	 * this.cSpringProps = new Properties();
	 * 
	 * for (Resource resource : locations) { try {
	 * this.cSpringProps.load(resource.getInputStream()); } catch (IOException e) {
	 * cLogger.error(e.getMessage());
	 * 
	 * this.setLcRes(cRes = false);
	 * 
	 * return cRes; } }
	 * 
	 * 
	 * // create the Spring container this.cBeanFactory = new
	 * XmlBeanFactory(beansDefinition);
	 * 
	 * PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new
	 * PropertyPlaceholderConfigurer();
	 * propertyPlaceholderConfigurer.setProperties(this.cSpringProps);
	 * propertyPlaceholderConfigurer.postProcessBeanFactory(this.cBeanFactory);
	 * 
	 * this.cBeanFactory.getBean("toolkitDataProvider");
	 * this.cBeanFactory.getBean("toolkitJAXB");
	 * 
	 * 
	 * return cRes;
	 * 
	 * } catch (Exception e) { cLogger.error("problem with configuration files!" +
	 * e.getMessage());
	 * 
	 * this.setLcRes(cRes = false);
	 * 
	 * return cRes; } }
	 */
	// ---
	/*
	 * protected boolean configureSpringExt() { boolean cRes = true;
	 * 
	 * try { this.applicationContext = new
	 * AnnotationConfigApplicationContext(ToolkitBeansConfig.class);
	 * 
	 * //this.applicationContext.getBean("toolkitDataProvider");
	 * //this.applicationContext.getBean("toolkitJAXB");
	 * 
	 * return cRes;
	 * 
	 * } catch (Exception e) { LOG.error("problem with configuration files!" +
	 * e.getMessage());
	 * 
	 * this.setLcRes(cRes = false);
	 * 
	 * return cRes; } }
	 */
	// ---
	/**
	 * @param cMappingFile
	 * @return File
	 */
	protected File getMappingFile(String cMappingFile) 
	{
		boolean cRes = true;

		String methodName = "";

		try
		{
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			File sfile = null;

			if ((null == cMappingFile) || (cMappingFile.equals(""))) {
				cRes = false;

				LOG.error(methodName + "::processRequest mappingFile path is wrong!");
			}

			if (cRes) 
			{
				sfile = new File(cMappingFile);

				if (!sfile.exists())
				{
					LOG.error(methodName + "::processRequest mappingFile file not exists:" + cMappingFile);

					this.setLcRes(cRes = false);
				}
			}

			return sfile;

		} 
		catch (Exception e) 
		{
			LOG.error(methodName + "::" + e.getMessage());

			this.setLcRes(cRes = false);

			return null;
		}
	}

	// ---

	/**
	 * @param cMethodParams
	 * @param cMethodResults
	 * @param cSourceName
	 * @return
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Category getRootCategory(String cSourceName, String cCategoryBrowseNodeId)
	{
		boolean cRes = true;

		String methodName = "";

		String sqlQuery = "";

		Session hbsSession = null;

		Transaction tx = null;

		List<Category> cCategories = new LinkedList<Category>();

		Category cCategory = new Category();

		try 
		{
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			if (null == cSourceName) {
				LOG.error(methodName + "::(null == cSourceName)");

				cRes = false;
			}

			if (null == cCategoryBrowseNodeId) {
				LOG.error(methodName + "::(null == cCategoryBrowseNodeId)");

				cRes = false;
			}

			// ------
			if (cRes) {
				if (null == this.cToolkitDataProvider) {
					LOG.error(methodName + "::cToolkitDataProvider is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(methodName);

				if (null == sqlQuery) {
					LOG.error(methodName + "::sqlQuery is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				hbsSession = cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();

				NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);
				cQuery.addEntity(Category.class);

				cQuery.setParameter("sourceName", cSourceName);
				cQuery.setParameter("categoryBrowseNodeId", cCategoryBrowseNodeId);

				tx = hbsSession.beginTransaction();

				cCategories = (List<Category>) cQuery.list();
			}
			// ------

			if (cRes) {
				if (cCategories != null && cCategories.size() >= 1) {
					cCategory = cCategories.get(0);
				}
			}

			if (tx != null) {
				tx.commit();
			}

			return cCategory;
		} 
		catch (Exception e)
		{
			LOG.error(methodName + "::" + e.getMessage());

			this.setLcRes(cRes = false);

			if (tx != null) {
				tx.rollback();
			}

			return new Category();
		} finally {

			if (hbsSession != null) {
				hbsSession.close();
			}
		}
	}

	// ---

	/**
	 * @param cMethodParams
	 * @param cMethodResults
	 * @param cSourceName
	 * @return
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Category> getRootCategories(String cSourceName) 
	{
		boolean cRes = true;

		String methodName = "";

		String sqlQuery = "";

		Session hbsSession = null;

		Transaction tx = null;

		List<Category> cDepartments = new LinkedList<Category>();

		try 
		{
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			if (null == cSourceName) {
				LOG.error(methodName + "::(null == cSourceName)");

				cRes = false;
			}

			// ------
			if (cRes) {
				if (null == this.cToolkitDataProvider) {
					LOG.error(methodName + "::cToolkitDataProvider is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(methodName/* , repParams */);

				if (null == sqlQuery) {
					LOG.error(methodName + "::sqlQuery is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				hbsSession = cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();

				NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);
				cQuery.addEntity(Category.class);

				cQuery.setParameter("sourceName", cSourceName);

				tx = hbsSession.beginTransaction();

				cDepartments = (List<Category>) cQuery.list();
			}
			// ------

			if (tx != null) {
				tx.commit();
			}

			return cDepartments;
		} catch (Exception e) {
			LOG.error(methodName + "::" + e.getMessage());

			this.setLcRes(cRes = false);

			if (tx != null) {
				tx.rollback();
			}

			return new LinkedList<Category>();
		} finally {
			if (hbsSession != null) {
				hbsSession.close();
			}
		}
	}

	// ---
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Node> getRootCategoriesNodes(String cSourceName)
	{
		boolean cRes = true;

		String methodName = "";

		String sqlQuery = "";

		Session hbsSession = null;

		Transaction tx = null;

		List<Node> cNodes = new LinkedList<Node>();

		try
		{
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			if (null == cSourceName) {
				LOG.error(methodName + "::(null == cSourceName)");

				cRes = false;
			}

			// ------
			if (cRes) {
				if (null == this.cToolkitDataProvider) {
					LOG.error(methodName + "::cToolkitDataProvider is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(methodName/* , repParams */);

				if (null == sqlQuery) {
					LOG.error(methodName + "::sqlQuery is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				hbsSession = cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();

				NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);
				cQuery.addEntity(Category.class);

				cQuery.setParameter("sourceName", cSourceName);

				tx = hbsSession.beginTransaction();

				cNodes = (List<Node>) cQuery.list();
			}
			// ------

			if (tx != null) {
				tx.commit();
			}

			return cNodes;
		} catch (Exception e) {
			LOG.error(methodName + "::" + e.getMessage());

			this.setLcRes(cRes = false);

			if (tx != null) {
				tx.rollback();
			}

			return new LinkedList<Node>();
		} finally {
			if (hbsSession != null) {
				hbsSession.close();
			}
		}
	}

	// ---
	/**
	 * @param cMethodParams
	 * @param cMethodResults
	 * @return
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean getRootCategories(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults)
	{
		boolean cRes = true;

		String methodName = "";

		String sqlQuery = "";

		String cSourceName = "";

		Session hbsSession = null;

		Transaction tx = null;

		try 
		{
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			if (null == cMethodParams) {
				LOG.error(methodName + "::cMethodParams is null!");

				cRes = false;
			}

			if (cRes) {
				cSourceName = (String) cMethodParams.get("p1");
			}

			if (null == cSourceName) {
				LOG.error(methodName + "::cSourceName is null!");

				cRes = false;
			}

			// ------
			if (cRes) {

				if (null == this.cToolkitDataProvider) {
					LOG.error(methodName + "::cToolkitDataProvider is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(methodName);

				if (null == sqlQuery) {
					LOG.error(methodName + "::sqlQuery is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				hbsSession = cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();

				NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);
				
				cQuery.addEntity(Category.class);
				cQuery.setParameter("sourceName", cSourceName);

				tx = hbsSession.beginTransaction();

				List<Category> cCategories = (List<Category>) cQuery.list();

				cMethodResults.put("r1", cCategories);
			}

			if (tx != null) {
				tx.commit();
			}

			return cRes;

		} catch (Exception e) {
			LOG.error(methodName + "::" + e.getMessage(), e);

			this.setLcRes(cRes = false);

			if (tx != null) {
				tx.rollback();
			}

			return cRes;
		} finally {
			if (hbsSession != null) {
				hbsSession.close();
			}
		}
	}

	/**
	 * @param cMethodParams
	 * @param cMethodResults
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean getRootCategoriesNodes(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults)
	{
		boolean cRes = true;

		String methodName = "";

		String sqlQuery = "";

		String cSourceName = "";

		Session hbsSession = null;

		Transaction tx = null;

		try 
		{
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			if (null == cMethodParams) {
				LOG.error(methodName + "::cMethodParams is null!");

				cRes = false;
			}

			if (cRes) {
				cSourceName = (String) cMethodParams.get("p1");
			}

			if (null == cSourceName) {
				LOG.error(methodName + "::cSourceName is null!");

				cRes = false;
			}

			// ------
			if (cRes) {

				if (null == this.cToolkitDataProvider) {
					LOG.error(methodName + "::cToolkitDataProvider is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(methodName);

				if (null == sqlQuery) {
					LOG.error(methodName + "::sqlQuery is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				hbsSession = this.cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();

				NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);

				cQuery.addEntity(Node.class);

				cQuery.setParameter("sourceName", cSourceName);

				tx = hbsSession.beginTransaction();

				List<Node> cNodes = (List<Node>) cQuery.list();

				cMethodResults.put("r1", cNodes);
			}

			if (tx != null) {
				tx.commit();
			}

			return cRes;

		} catch (Exception e) {
			LOG.error(methodName + "::" + e.getMessage());

			this.setLcRes(cRes = false);

			if (tx != null) {
				tx.rollback();
			}

			return cRes;
		} finally {
			if (hbsSession != null) {
				hbsSession.close();
			}
		}
	}

	/**
	 * @param cMethodParams
	 * @param cMethodResults
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean getRootCategorySubNodes(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults)
	{
		boolean cRes = true;

		String methodName = "";

		String sqlQuery = "";

		String cSourceName = "";

		String cBrowseNode = "";

		Session hbsSession = null;

		Transaction tx = null;

		try {
			this.iReflection = new ToolkitReflection();

			methodName = this.iReflection.getMethodName();

			if (null == cMethodParams) {
				LOG.error(methodName + "::cMethodParams is null!");

				cRes = false;
			}
			// ---
			if (cRes) {
				cSourceName = (String) cMethodParams.get("p1");
			}

			if (null == cSourceName) {
				LOG.error(methodName + "::cSourceName is null!");

				cRes = false;
			}
			// ---
			if (cRes) {
				cBrowseNode = (String) cMethodParams.get("p2");
			}

			if (null == cBrowseNode) {
				LOG.error(methodName + "::cBrowseNode is null!");

				cRes = false;
			}
			// ------

			// ------
			if (cRes) {

				if (null == this.cToolkitDataProvider) {
					LOG.error(methodName + "::cToolkitDataProvider is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(methodName);

				if (null == sqlQuery) {
					LOG.error(methodName + "::sqlQuery is NULL for the Method:" + methodName);

					cRes = false;
				}
			}
			// ------
			if (cRes) {
				hbsSession = this.cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();

				NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);

				cQuery.addEntity(Node.class);

				cQuery.setParameter("sourceName", cSourceName);
				cQuery.setParameter("browseNode", cBrowseNode);

				tx = hbsSession.beginTransaction();

				List<Node> cNodes = (List<Node>) cQuery.list();

				cMethodResults.put("r1", cNodes);
			}

			if (tx != null) {
				tx.commit();
			}

			return cRes;

		} catch (Exception e) {
			LOG.error(methodName + "::" + e.getMessage());

			this.setLcRes(cRes = false);

			if (tx != null) {
				tx.rollback();
			}

			return cRes;
		} finally {
			if (hbsSession != null) {
				hbsSession.close();
			}
		}
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean handleBrowseNodesHierarchy() {
		return true;
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean handleNodeLookup(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean handleCategoryNodeLookup(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean handleItemSearchList(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean getNodeLookup(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean getCategoryNodeLookup(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean getItemSearchList(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean saveCategoryNode(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean saveNodeLookup(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean saveCategoryNodeLookup(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) {
		// TODO Auto-generated method stub
		return true;
	}
}
