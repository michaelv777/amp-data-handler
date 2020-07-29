/**
 * 
 */
package com.amp.data.handler.test;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.amp.common.api.impl.ToolkitConstants;
import com.amp.data.handler.aws.DataHandlerAWS;
import com.amp.data.handler.base.DataHandlerBase;
import com.amp.data.handler.test.config.DataHandlerBeansConfig;
import com.amp.jpa.entities.Category;
import com.amp.jpa.entities.Node;

/**
 * @author MVEKSLER
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ComponentScan(basePackages = {"com.amp.data.handler"})
@ContextConfiguration(classes = {DataHandlerBeansConfig.class})
@TestPropertySource(locations = { "classpath:amp-common-api.properties"})
public class DataHandlerTest 
{
	protected long cSourceId = 101;
	
	@SuppressWarnings("unused")
	@Autowired
	private Environment env;
	
	@Autowired
	protected DataHandlerAWS cDataHandlerAWS = null;
	/**
	 * @param name
	 */
	public DataHandlerTest() {
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		
	}

	/**
	 * Test method for {@link amp.data.handler.aws.DataHandlerAWS#MappingHandler()}.
	 */
//	@Ignore @Test
//	public void testMappingHandler() 
//	{
//		try
//		{
//			this.cDataHandler = new DataHandlerAWS();
//			
//			if ( !this.cDataHandler.isLcRes() )
//			{
//				fail("testMappingHandler failed build MappingHandler object!");
//			}
//		}
//		catch( Exception e)
//		{
//			fail("testToolkitDataProvider failed:" + e.getStackTrace());
//		}
//	}

	/**
	 * Test method for {@link com.amp.data.handler.aws.DataHandlerAWS#MappingHandler()}.
	 */
	@org.junit.Ignore
	@Test
	public void handleBrowseNodesHierarchy() 
	{
		boolean cRes = true;
		
		//DataHandlerBase cOperationsBeanAWS = null;
		
		try
		{
			//-----------------------------------------
		    List<Category> cCategories = new LinkedList<Category>();
		   
		    List<Node> cBrowseNodesResults = new LinkedList<Node>();
		    
		    Queue<Node> cNodesQueue = new LinkedList<Node>();
		    
		    //cDataHandlerAWS = DataHandlerFactory.getDataHandler(DataHandlerAWS.class);
		    
		    if ( null == cDataHandlerAWS )
    		{
		    	cRes = false;
		    	
		    	fail("this.cDataHandlerAWS id null  for cDataHandlerAWS.class!!");
    		}
		    //---get Categories----------
		    cCategories = this.handleCategories( cDataHandlerAWS );
		    
		    Thread.sleep(5000);
		    
		    //---get first level Nodes-----
		    cBrowseNodesResults = this.handleCategoriesNodes(cDataHandlerAWS, cCategories);
		    
		    for( Node cBrowseNode : cBrowseNodesResults)
        	{
	    		cNodesQueue.add(cBrowseNode);
        	}
		    
		    Thread.sleep(5000);
		    //---run BFS to get all level Nodes-----
		    if ( cRes )
		    {
		    	while( !cNodesQueue.isEmpty() )
		    	{
		    		Thread.sleep(1000);
		    		
		    		Node cBrowseNode = cNodesQueue.remove();
		    		
		    		System.out.println("Queue size=" + cNodesQueue.size());
		    		
		    		cBrowseNodesResults = this.handleNode(cDataHandlerAWS, cBrowseNode);
		    		
		    		for( Node cBrowseNodeNext : cBrowseNodesResults)
			    	{
			    		cNodesQueue.add(cBrowseNodeNext);
			    	}
			        //--------
		    	}
		    }
		}
		catch( Exception e)
		{
			fail("testToolkitDataProvider failed:" + e.getStackTrace());
		}
	}

	/**
	 * @param cOperationsBeanAWS
	 * @param cMethodParams
	 * @param cMethodResults
	 * @param cNodesQueue
	 * @param cLevel
	 * @param cType
	 * @param cBrowseNode
	 */
	@SuppressWarnings("unchecked")
	protected List<Node> handleNode(DataHandlerBase cOperationsBeanAWS, Node cBrowseNode) 
	{
		boolean cRes = true;
		
		List<Node> cBrowseNodesResults = new LinkedList<Node>();
	
		try
		{
			//-----------------------------------------
			Map<String, Object> cMethodParams = new HashMap<String, Object>();
		    Map<String, Object> cMethodResults = new HashMap<String, Object>();
		    
		    
			Long cLevel = new Long(3L);
	    	Long cType = new Long(ToolkitConstants.AMP_CLASSIFICATION_NODE_ID);
	    	
			HashMap<String, String> cParams = new HashMap<String, String>();
			cParams.put(ToolkitConstants.BROWSE_NODE_ID_PARAM, cBrowseNode.getBrowsenodeid());
			
			cMethodParams.put("p1", cParams);
			cMethodParams.put("p2", cLevel);
			cMethodParams.put("p3", cType);
			cMethodParams.put("p4", cBrowseNode);
			
			cRes = cOperationsBeanAWS.handleNodeLookup(cMethodParams, cMethodResults);
			
			if ( !cRes )
			{
				//fail("testMappingHandler failed build MappingHandler object!");
			}
			
			//--------
			if ( cRes )
			{
				cBrowseNodesResults = (LinkedList<Node>)cMethodResults.get("r1");
				
				if ( null == cBrowseNodesResults )
			    {
			    	cRes = false;
			    	
			    	fail("cBrowseNodesResults is null!");
			    }
			}
			
			return cBrowseNodesResults;
		}
		catch( Exception e)
		{
			fail("testToolkitDataProvider failed:" + e.getStackTrace());
			
			return new LinkedList<Node>();
		}
	}

	/**
	 * @param cRes
	 * @param cOperationsBeanAWS
	 * @param cMethodParams
	 * @param cMethodResults
	 * @param cCategories
	 * @param cNodesQueue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Node> handleCategoriesNodes(
			DataHandlerBase cOperationsBeanAWS,
			List<Category> cCategories) 
	{
		boolean cRes = true;
		
		List<Node> cBrowseNodesResults = new LinkedList<Node>();
		
		try
		{
			//-----------------------------------------
			Map<String, Object> cMethodParams = new HashMap<String, Object>();
		    Map<String, Object> cMethodResults = new HashMap<String, Object>();
		    
			if ( cRes )
			{
				for( Category cCategory : cCategories )
				{
				    HashMap<String, String> cParams = new HashMap<String, String>();
				    cParams.put(ToolkitConstants.BROWSE_NODE_ID_PARAM, cCategory.getRootbrowsenode());
				    
				    Long cLevel = new Long(2L);
				    Long cType = new Long(ToolkitConstants.AMP_CATEGORY_NODE_ID);
				    
				    cMethodParams.put("p1", cParams);
				    cMethodParams.put("p2", cLevel);
				    cMethodParams.put("p3", cType);
				    cMethodParams.put("p4", cCategory);
				    
					cRes = cOperationsBeanAWS.handleCategoryNodeLookup(cMethodParams, cMethodResults);
					
					if ( !cRes )
					{
						fail("testMappingHandler failed build MappingHandler object!");
					}
					else
					{
						cBrowseNodesResults.addAll((LinkedList<Node>)cMethodResults.get("r1"));
					}
				}
			}
			return cBrowseNodesResults;
		}
		catch( Exception e)
		{
			fail("testToolkitDataProvider failed:" + e.getStackTrace());
			
			return new LinkedList<Node>();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<Category> handleCategories(DataHandlerBase cOperationsBeanAWS )
	{
		boolean cRes = true;
		
		List<Category> cCategories = new LinkedList<Category>();
		
		try
		{
			//-----------------------------------------
			Map<String, Object> cMethodParams = new HashMap<String, Object>();
		    Map<String, Object> cMethodResults = new HashMap<String, Object>();
		    
			if ( cRes )
		    {
		    	cMethodParams.put("p1", "AmazonUS");
		    	
				cRes = cOperationsBeanAWS.getRootCategories(cMethodParams, cMethodResults);
				
				if ( !cRes )
				{
					fail("testMappingHandler failed to run getRootCategories!");
				}
				
				if ( cRes )
		        {
		        	cCategories =  (List<Category>)cMethodResults.get("r1");
		        	
		        	if ( null == cCategories )
		        	{
		        		fail("cCategories list is null!");
		        	}
		        	
		        	//---save categories to node table ( use FK from child to parent node ) 
				    for( Category cCategory : cCategories )
				    {
				    	cMethodParams.put("p1", cCategory);
				    	
				    	cOperationsBeanAWS.handleCategoryNode(cMethodParams, cMethodResults);
				    }
		        }
		    }
			
			return cCategories;
		}
		catch( Exception e)
		{
			fail("testToolkitDataProvider failed:" + e.getStackTrace());
			
			return new LinkedList<Category>();
		}
	}
	
//	//------------------------------------------------------------------------------------
//	/**
//	 * Test method for {@link amp.data.handler.aws.DataHandlerAWS#MappingHandler()}.
//	 */
//	@Ignore @Test
//	public void testHandleCategoryNodeLookup() 
//	{
//		boolean cRes = true;
//		
//		try
//		{
//			Map<String, Object> cMethodParams = new HashMap<String, Object>();
//		    Map<String, Object> cMethodResults = new HashMap<String, Object>();
//		       	 
//		    HashMap<String, String> params = new HashMap<String, String>();
//		    params.put(ToolkitConstants.BROWSE_NODE_ID_PARAM, "677211011");
//		    
//		    cMethodParams.put("p1", params);
//		    
//		    DataHandlerBase cOperationsBeanAWS = 
//		    		DataHandlerFactory.getDataHandler(DataHandlerAWS.class);
//    		
//    		if ( null == cOperationsBeanAWS )
//    		{
//    			System.out.println(
//    					"this.cE2EDataHandlerBase id null  for E2EDataHandlerRM.class!!");
//    			
//    			fail("cDataHandlerBase is null!");
//    		}
//    		
//			cRes = cOperationsBeanAWS.handleNodeLookup(cMethodParams, cMethodResults);
//			
//			if ( !cRes )
//			{
//				fail("testMappingHandler failed build MappingHandler object!");
//			}
//		}
//		catch( Exception e)
//		{
//			fail("testToolkitDataProvider failed:" + e.getStackTrace());
//		}
//	}
}
