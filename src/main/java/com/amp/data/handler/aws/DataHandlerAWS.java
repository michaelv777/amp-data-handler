/**
 * 
 */
package com.amp.data.handler.aws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amp.amazon.webservices.rest.BrowseNode;
import com.amp.amazon.webservices.rest.BrowseNodes;
import com.amp.amazon.webservices.rest.Items;
import com.amp.amazon.webservices.rest.client.AmazonClientWorker;
import com.amp.common.api.impl.ToolkitConstants;
import com.amp.common.api.impl.ToolkitReflection;
import com.amp.data.handler.base.DataHandlerBase;
import com.amp.jpa.entities.Category;
import com.amp.jpa.entities.Node;
import com.amp.jpa.entities.Nodeshierarchy;
import com.amp.jpa.entities.NodeshierarchyPK;
import com.amp.jpa.entities.NodetypeM;

/**
 * @author MVEKSLER
 *
 */

@Component
public class DataHandlerAWS extends DataHandlerBase
{
	private static final Logger LOG = 
			LoggerFactory.getLogger(DataHandlerAWS.class);
	
	private ToolkitReflection iReflection = null;
	
	//---getters/setters---
	
	//---class methods---
	
	public DataHandlerAWS() 
	{
		String methodName = "";
		
		try
    	{
			this.iReflection = new ToolkitReflection();
    		
			methodName = this.iReflection.getMethodName();
			
    	}    		
    	catch( Exception e)
    	{
    		LOG.error(methodName + "::" + e.getMessage(), e);
    		
    		this.setLcRes(false);
    	}
	}
	
	public DataHandlerAWS(HashMap<String, String> cSystemConfiguration,
						  HashMap<String, String> cWorkerConfiguration) 
	{
		String methodName = "";
		
		try
    	{
			this.iReflection = new ToolkitReflection();
    		
			methodName = this.iReflection.getMethodName();
			
			this.setcSystemConfiguration(cSystemConfiguration);
			
			this.setcWorkerConfiguration(cWorkerConfiguration);
    	}    		
    	catch( Exception e)
    	{
    		LOG.error(methodName + "::" + e.getMessage(), e);
    		
    		this.setLcRes(false);
    	}
	}
	
	//---
	@PostConstruct
	public boolean init()
	{
		super.init();
		
		boolean cRes = true;
		
		String  methodName = "";
	
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        methodName = ste.getMethodName();
			
			return cRes;	 
		}
		catch( Exception e)
		{
			LOG.error(methodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean handleNodeLookup(Map<String, Object> cMethodParams,
			  	  				    Map<String, Object> cMethodResults)
    {
    	boolean cRes = true;
    	
    	String cMethodName = "";
    	
    	try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodParams )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + 
	        			"::cMethodParams is null. Check Map<String, Object> cMethodParams parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cRes = this.getNodeLookup(cMethodParams, cMethodResults);
	        }

	        //--------
	        if ( cRes )
	        {
	        	cRes = this.saveNodeLookup(cMethodParams, cMethodResults);
	        }
	        
	        LOG.info("------------------");
	        
	        return cRes;
    	}
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
    }
	
	/*-----------------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	@Override
	public boolean getNodeLookup(Map<String, Object> cMethodParams,
			  	  				 Map<String, Object> cMethodResults)
    {
    	boolean cRes = true;
    	
    	String cMethodName = "";
    	
    	String cParentBrowseNodeId = "";
    	
    	HashMap<String, String> cParams = new HashMap<String, String>();
    	
    	List<BrowseNodes> cBrowseNodes = new ArrayList<BrowseNodes>();
    	
    	List<Node> cBrowseNodesResults = new LinkedList<Node>();
    	
    	AmazonClientWorker cAmazonClientWorker = null;
    	
    	Long cLevel = new Long(-1L);
    	Long cType = new Long(-1L);
    	
    	try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodParams )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Check Map<String, Object> cMethodParams parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cParams = (HashMap<String, String>)cMethodParams.get("p1");
	        	cLevel = (Long)cMethodParams.get("p2");
	        	cType = (Long)cMethodParams.get("p3");
	        	
	        	if ( null == cParams )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::params is null. "
	 	        							  + "Check HashMap<String, String> params parameter!");
	 	        }
	        	
	        	if ( null == cLevel )
	        	{
	        		cLevel = new Long(ToolkitConstants.AMP_UNDEFINED_LEVEL);
	        	}
	        	
	        	if ( null == cType )
	        	{
	        		cType = new Long(ToolkitConstants.AMP_UNDEFINED_NODE_ID);
	        	}
	        	
	        	if ( cRes )
	        	{
	        		cParentBrowseNodeId = cParams.get(ToolkitConstants.BROWSE_NODE_ID_PARAM);
	        		
	        		if ( ( null == cParentBrowseNodeId) || ( cParentBrowseNodeId.equals("")) )
	        		{
	        			cRes = false;
		 	        	
		 	        	LOG.error(cMethodName + "::cParentBrowseNodeId is null. "
		 	        							  + "Check HashMap<String, String> params parameter!");
	        		}
	        	}
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cAmazonClientWorker = new AmazonClientWorker(
	        			this.cSystemConfiguration, 
	        			this.cWorkerConfiguration);
	        	
	        	cBrowseNodes = cAmazonClientWorker.browseNodeLookupList(cParams);
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	for( BrowseNodes cBrowseNodesList : cBrowseNodes)
	        	{
	        		List<BrowseNode> cBrowseNodeList = cBrowseNodesList.getBrowseNode();
	        		
	        		for( BrowseNode cBrowseNode : cBrowseNodeList )
	        		{
	        			BrowseNode.Children cChildren = cBrowseNode.getChildren();
	        			
	        			List<BrowseNode> cChildrenNodeList = cChildren.getBrowseNode();
	        			
	        			for( BrowseNode cChildBrowseNode : cChildrenNodeList )
	        			{
		        			Node cBrowseNodeData = new Node();
		        			
		        			cBrowseNodeData.setBrowsenodeid(cChildBrowseNode.getBrowseNodeId());
		        			
		        			cBrowseNodeData.setName(cChildBrowseNode.getName());
		        			
		        			Node parent = new Node();
		        			parent.setBrowsenodeid(cParentBrowseNodeId);
		        			cBrowseNodeData.setNode(parent);
		        			//cBrowseNodeData.setNode2parentbrowsenode(cParentBrowseNodeId);
		        			
		        			NodetypeM cNodeType = new NodetypeM();
		        			cNodeType.setNodetypeid(cType.longValue());
		        			
		        			cBrowseNodeData.setNodelevel(cLevel.longValue());
		        			
		        			cBrowseNodeData.setNodetypeM(cNodeType);
		        			
		        			cBrowseNodesResults.add(cBrowseNodeData);
	        			}
	        		}
	        	}
	        }
	        //--------
	        if ( cRes )
	        {
	        	cMethodResults.put("r1", cBrowseNodesResults);
	        }
	        
	        LOG.info("------------------");
	        
	        return cRes;
    	}
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
    }
	/*-----------------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	@Override
	public boolean handleBrowseNodesHierarchy() 
	{
		boolean cRes = true;
		
		String cMethodName = "";
		
		try
		{
			//--------------------------
			Map<String, Object> cMethodParams = new HashMap<String, Object>();
		    Map<String, Object> cMethodResults = new HashMap<String, Object>();
		       	
		    List<Category> cCategories = new LinkedList<Category>();
		    List<Node> cBrowseNodesResults = new LinkedList<Node>();
		    
		    Queue<Node> cNodesQueue = new LinkedList<Node>();
		    
		    //---get Categories----------
		    if ( cRes )
		    {
		    	cMethodParams.put("p1", "Amazon");
		    	
				cRes = this.getRootCategories(cMethodParams, cMethodResults);
				
				if ( !cRes )
				{
					LOG.error(cMethodName + "testMappingHandler failed build MappingHandler object!");
				}
				
				if ( cRes )
		        {
		        	cCategories =  (List<Category>)cMethodResults.get("r1");
		        	
		        	if ( null == cCategories )
		        	{
		        		LOG.error(cMethodName + "cCategories list is null!");
		        	}
		        }
		    }
		    
		    //---get first level Nodes-----
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
				    
					cRes = this.handleCategoryNodeLookup(cMethodParams, cMethodResults);
					
					if ( !cRes )
					{
						LOG.error(cMethodName + "testMappingHandler failed build MappingHandler object!");
					}
					else
					{
						cBrowseNodesResults = (LinkedList<Node>)cMethodResults.get("r1");
						
						for( Node cBrowseNode : cBrowseNodesResults)
			        	{
				    		cNodesQueue.add(cBrowseNode);
			        	}
					}
		    	}
		    }
		    
		    //---run BFS to get all level Nodes-----
		    if ( cRes )
		    {
		    	Long cLevel = new Long(3L);
		    	Long cType = new Long(ToolkitConstants.AMP_CLASSIFICATION_NODE_ID);
		    	
		    	while( !cNodesQueue.isEmpty() )
		    	{
		    		Node cBrowseNode = cNodesQueue.remove();
		    		
		    		HashMap<String, String> cParams = new HashMap<String, String>();
				    cParams.put(ToolkitConstants.BROWSE_NODE_ID_PARAM, cBrowseNode.getBrowsenodeid());
				    
				    cMethodParams.put("p1", cParams);
				    cMethodParams.put("p2", cLevel);
				    cMethodParams.put("p3", cType);
		    		
					cRes = this.handleNodeLookup(cMethodParams, cMethodResults);
					
					if ( !cRes )
					{
						LOG.error(cMethodName + "testMappingHandler failed build MappingHandler object!");
					}
					
					//--------
			        if ( cRes )
			        {
			        	cBrowseNodesResults = (LinkedList<Node>)cMethodResults.get("r1");
			        	
			        	if ( null == cBrowseNodesResults )
			 	        {
			 	        	cRes = false;
			 	        	
			 	        	LOG.error(cMethodName + "cBrowseNodesResults is null!");
			 	        }
			        	else
			        	{
			        		for( Node cBrowseNodeNext : cBrowseNodesResults)
				        	{
					    		cNodesQueue.add(cBrowseNodeNext);
				        	}
			        	}
			        }
			        //--------
			        if ( cRes )
			        {
			        	cLevel += 1;
			        }
		    	}
		    }
		    
		    LOG.info("------------------");
		    
		    return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		this.setLcRes(cRes = false);
    		
    		return cRes;
		}
	}
	
	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean handleCategoryNode(Map<String, Object> cMethodParams,
			  	  				      Map<String, Object> cMethodResults)
    {
    	boolean cRes = true;
    	
    	String cMethodName = "";
    	
    	try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodParams )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Check Map<String, Object> cMethodParams parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cRes = this.saveCategoryNode(cMethodParams, cMethodResults);
	        }
	        
	        LOG.info("------------------");
	        
	        return cRes;
    	}
    	
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
    }
	
	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean handleCategoryNodeLookup(Map<String, Object> cMethodParams,
			  	  				       	    Map<String, Object> cMethodResults)
    {
    	boolean cRes = true;
    	
    	String cMethodName = "";
    	
    	try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodParams )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Check Map<String, Object> cMethodParams parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cRes = this.getCategoryNodeLookup(cMethodParams, cMethodResults);
	        }

	        //--------
	        if ( cRes )
	        {
	        	cRes = this.saveCategoryNodeLookup(cMethodParams, cMethodResults);
	        }
	        
	        LOG.info("------------------");
	        
	        return cRes;
    	}
    	
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
    }
	/*-----------------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	@Override
	public boolean getCategoryNodeLookup(Map<String, Object> cMethodParams,
			  	  				         Map<String, Object> cMethodResults)
    {
    	boolean cRes = true;
    	
    	String cMethodName = "";
    	
    	Long cLevel = new Long(-1L);
    	Long cType = new Long(-1L);
    	
    	Category cCategory = new Category();
    	
    	String cParentBrowseNodeId = "";
    	
    	HashMap<String, String> cParams = new HashMap<String, String>();
    	
    	List<BrowseNodes> cBrowseNodes = new ArrayList<BrowseNodes>();
    	
    	List<Node> cBrowseNodesResults = new LinkedList<Node>();
    	
    	AmazonClientWorker cAmazonClientWorker = null;
    	
    	try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodParams )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Check Map<String, Object> cMethodParams parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cParams = (HashMap<String, String>)cMethodParams.get("p1");
	        	cLevel = (Long)cMethodParams.get("p2");
	        	cType = (Long)cMethodParams.get("p3");
	        	cCategory = (Category)cMethodParams.get("p4"); 
	        	
	        	if ( null == cParams )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::params is null. "
	 	        							  + "Check HashMap<String, String> params parameter!");
	 	        }
	        	
	        	if ( null == cCategory )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::cCategory is null. "
	 	        							  + "Check cCategory parameter!");
	 	        }
	        	
	        	if ( null == cLevel )
	        	{
	        		cLevel = new Long(ToolkitConstants.AMP_UNDEFINED_LEVEL);
	        	}
	        	
	        	if ( null == cType )
	        	{
	        		cType = new Long(ToolkitConstants.AMP_UNDEFINED_NODE_ID);
	        	}
	        	
	        	if ( cRes )
	        	{
	        		cParentBrowseNodeId = cParams.get(ToolkitConstants.BROWSE_NODE_ID_PARAM);
	        		
	        		if ( ( null == cParentBrowseNodeId) || ( cParentBrowseNodeId.equals("")) )
	        		{
	        			cRes = false;
		 	        	
		 	        	LOG.error(cMethodName + "::cParentBrowseNodeId is null. "
		 	        							  + "Check HashMap<String, String> params parameter!");
	        		}
	        	}
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cAmazonClientWorker = new AmazonClientWorker(
	        			this.cSystemConfiguration, 
	        			this.cWorkerConfiguration);
	        	
	        	cBrowseNodes = cAmazonClientWorker.browseNodeLookupList(cParams);
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	for( BrowseNodes cBrowseNodesList : cBrowseNodes)
	        	{
	        		List<BrowseNode> cBrowseNodeList = cBrowseNodesList.getBrowseNode();
	        		
	        		for( BrowseNode cBrowseNode : cBrowseNodeList )
	        		{
	        			BrowseNode.Children cChildren = cBrowseNode.getChildren();
	        			
	        			List<BrowseNode> cChildrenNodeList = cChildren.getBrowseNode();
	        			
	        			for( BrowseNode cChildBrowseNode : cChildrenNodeList )
	        			{
		        			Node cBrowseNodeData = new Node();
		        			
		        			cBrowseNodeData.setBrowsenodeid(cChildBrowseNode.getBrowseNodeId());
		        			
		        			cBrowseNodeData.setName(cChildBrowseNode.getName());
		        			
		        			//cBrowseNodeData.setNode2parentbrowsenode(cParentBrowseNodeId);
		        			Node parent = new Node();
		        			parent.setBrowsenodeid(cParentBrowseNodeId);
		        			cBrowseNodeData.setNode(parent);
		        			
		        			NodetypeM cNodeType = new NodetypeM();
		        			cNodeType.setNodetypeid(cType.longValue());
		        			
		        			cBrowseNodeData.setNodetypeM(cNodeType);
		        			
		        			cBrowseNodeData.setNodelevel(cLevel.longValue());
		        			
		        			cBrowseNodeData.setSource(cCategory.getSource());
		        			
		        			cBrowseNodesResults.add(cBrowseNodeData);
	        			}
	        		}
	        	}
	        }
	        //--------
	        if ( cRes )
	        {
	        	cMethodResults.put("r1", cBrowseNodesResults);
	        }
	        
	        LOG.info("------------------");
	        
	        return cRes;
    	}
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
    }
	/*-----------------------------------------------------------------------------------*/
	@Override
	public boolean saveCategoryNode(Map<String, Object> cMethodParams,
			  	  				    Map<String, Object> cMethodResults)
	{
		boolean cRes = true;
		
		String cMethodName = "";
	
		Session hbsSession = null;
		
		Transaction tx = null;
		
		Category cCategory = null;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodResults )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Map<String, Object> cMethodResults parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cCategory = (Category)cMethodParams.get("p1");
	        	
	        	if ( null == cCategory )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::cCategory is null. "
	 	        							  + "Check HashMap<String, String> params parameter!");
	 	        }
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	hbsSession = cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();
				
				tx = hbsSession.beginTransaction();
				
				NodetypeM cNodeType = new NodetypeM();
				cNodeType.setNodetypeid(1);
				cNodeType.setIscategoryroot(1);
				cNodeType.setName("Root Catogery");
				
				
				Node cCategoryNode = new Node();
				cCategoryNode.setBrowsenodeid(cCategory.getRootbrowsenode());
				cCategoryNode.setName(cCategory.getName());
				cCategoryNode.setNodelevel(1);
				cCategoryNode.setNodetypeM(cNodeType);
				cCategoryNode.setSource(cCategory.getSource());
				
				hbsSession.saveOrUpdate(cCategoryNode);
	        }
	
	        tx.commit();
	        
	        LOG.info("------------------");
	        
	        return cRes;
		}
		
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			this.setLcRes(cRes = false);
			
			if ( tx != null )
			{
				tx.rollback();
			}
			
			return cRes;
		}
		finally
		{
			
			if ( hbsSession != null )
			{
				hbsSession.close();
			}
		}
	}

	/*-----------------------------------------------------------------------------------*/
	@Override
	@SuppressWarnings("unchecked")
	public boolean saveCategoryNodeLookup(Map<String, Object> cMethodParams,
			  	  				          Map<String, Object> cMethodResults)
	{
		boolean cRes = true;
		
		String cMethodName = "";
	
		String cParentBrowseNodeId = "";
			
		List<Node> cBrowseNodesResults = new LinkedList<Node>();
		
		Session hbsSession = null;
		
		Transaction tx = null;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodResults )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Map<String, Object> cMethodResults parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	HashMap<String, String> cParams = (HashMap<String, String>)cMethodParams.get("p1");
	        	
	        	if ( null == cParams )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::params is null. "
	 	        							  + "Check HashMap<String, String> params parameter!");
	 	        }
	        	
	        	if ( cRes )
	        	{
	        		cParentBrowseNodeId = cParams.get(ToolkitConstants.BROWSE_NODE_ID_PARAM);
	        		
	        		if ( ( null == cParentBrowseNodeId) || ( cParentBrowseNodeId.equals("")) )
	        		{
	        			cRes = false;
		 	        	
		 	        	LOG.error(cMethodName + "::cParentBrowseNodeId is null. "
		 	        							  + "Check HashMap<String, String> params parameter!");
	        		}
	        	}
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cBrowseNodesResults = (LinkedList<Node>)cMethodResults.get("r1");
	        	
	        	if ( null == cBrowseNodesResults )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::cBrowseNodesResults is null. "
	 	        							  + "Map<String, Object> cMethodResults parameter!");
	 	        }
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	hbsSession = cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();
				
				tx = hbsSession.beginTransaction();
				
	        	for( Node cBrowseNode : cBrowseNodesResults)
	        	{
	        		Nodeshierarchy cNodesHierarchy = new Nodeshierarchy();
	        		
	        		NodeshierarchyPK cNodesHierarchyPK = new NodeshierarchyPK();
	        		
	        		cNodesHierarchyPK.setNode2parentbrowsenode(cParentBrowseNodeId);
	        		
	        		cNodesHierarchyPK.setNode2childbrowsenode(cBrowseNode.getBrowsenodeid());
	        		
	        		cNodesHierarchy.setId(cNodesHierarchyPK);
	        		
	        		hbsSession.saveOrUpdate(cBrowseNode);
	        		
	        		hbsSession.saveOrUpdate(cNodesHierarchy);
	        	}
	        }
	
	        tx.commit();
	        
	        LOG.info("------------------");
	        
	        return cRes;
		}
		
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			this.setLcRes(cRes = false);
			
			if ( tx != null )
			{
				tx.rollback();
			}
			
			return cRes;
		}
		finally
		{
			
			if ( hbsSession != null )
			{
				hbsSession.close();
			}
		}
	}

	/*-----------------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	@Override
	public boolean saveNodeLookup(Map<String, Object> cMethodParams,
			  	  				  Map<String, Object> cMethodResults)
	{
		boolean cRes = true;
		
		String cMethodName = "";
	
		String cParentBrowseNodeId = "";
			
		List<Node> cBrowseNodesResults = new LinkedList<Node>();
		
		Session hbsSession = null;
		
		Transaction tx = null;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        Node cParentNode = new Node();
	        //--------
	        if ( null == cMethodResults )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Map<String, Object> cMethodResults parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	HashMap<String, String> cParams = (HashMap<String, String>)cMethodParams.get("p1");
	        	Long cLevel = (Long)cMethodParams.get("p2");
	        	Long cType = (Long)cMethodParams.get("p3");
	        	cParentNode = (Node)cMethodParams.get("p4");
	        	
	        	if ( null == cParams )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::params is null. "
	 	        							  + "Check HashMap<String, String> params parameter!");
	 	        }
	        	
	        	if ( null == cLevel )
	        	{
	        		cLevel = new Long(ToolkitConstants.AMP_UNDEFINED_LEVEL);
	        	}
	        	
	        	if ( null == cType )
	        	{
	        		cType = new Long(ToolkitConstants.AMP_UNDEFINED_NODE_ID);
	        	}
	        	
	        	if ( null == cParentNode )
	        	{
	        		LOG.error(cMethodName + "::cParentNode is null.");
	        		
	        		cRes = false;
	        	}
	        	
	        	if ( cRes )
	        	{
	        		cParentBrowseNodeId = cParams.get(ToolkitConstants.BROWSE_NODE_ID_PARAM);
	        		
	        		if ( ( null == cParentBrowseNodeId) || ( cParentBrowseNodeId.equals("")) )
	        		{
	        			cRes = false;
		 	        	
		 	        	LOG.error(cMethodName + "::cParentBrowseNodeId is null. "
		 	        							  + "Check HashMap<String, String> params parameter!");
	        		}
	        	}
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cBrowseNodesResults = (LinkedList<Node>)cMethodResults.get("r1");
	        	
	        	if ( null == cBrowseNodesResults )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::cBrowseNodesResults is null. "
	 	        							  + "Map<String, Object> cMethodResults parameter!");
	 	        }
	        }
	       
	        //--------
	        if ( cRes )
	        {
	        	hbsSession = cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();
				
				tx = hbsSession.beginTransaction();
				
	        	for( Node cBrowseNode : cBrowseNodesResults)
	        	{
	        		cBrowseNode.setSource(cParentNode.getSource());
	        		
	        		Nodeshierarchy cNodesHierarchy = new Nodeshierarchy();
	        		
	        		NodeshierarchyPK cNodesHierarchyPK = new NodeshierarchyPK();
	        		
	        		cNodesHierarchyPK.setNode2parentbrowsenode(cParentBrowseNodeId);
	        		
	        		cNodesHierarchyPK.setNode2childbrowsenode(cBrowseNode.getBrowsenodeid());
	        		
	        		cNodesHierarchy.setId(cNodesHierarchyPK);
	        		
	        		hbsSession.saveOrUpdate(cBrowseNode);
	        		
	        		hbsSession.saveOrUpdate(cNodesHierarchy);
	        	}
	        }
	
	        //--------
	        LOG.info("------------------");
	        
	        if ( tx != null )
			{
				tx.commit();
			}
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			this.setLcRes(cRes = false);
			
			if ( tx != null )
			{
				tx.rollback();
			}
			
			return cRes;
		}
		finally
		{
			
			if ( hbsSession != null )
			{
				hbsSession.close();
			}
		}
	}

	@Override
	public boolean handleItemSearchList(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) 
	{
		boolean cRes = true;
    	
    	String cMethodName = "";
    	
    	try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodParams )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Check Map<String, Object> cMethodParams parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cRes = this.getItemSearchList(cMethodParams, cMethodResults);
	        }
	      
	        LOG.info("------------------");
	        
	        return cRes;
    	}
    	
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean getItemSearchList(Map<String, Object> cMethodParams, Map<String, Object> cMethodResults) 
	{
		boolean cRes = true;
    	
    	String cMethodName = "";
    	
    	HashMap<String, String> cParams = new HashMap<String, String>();
    	
    	List<Items> cItemSearchResults = new LinkedList<Items>();
    	
    	AmazonClientWorker cAmazonClientWorker = null;
    	
    	try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--------
	        if ( null == cMethodParams )
	        {
	        	cRes = false;
	        	
	        	LOG.error(cMethodName + "::cMethodParams is null. "
	        							  + "Check Map<String, Object> cMethodParams parameter!");
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cParams = (HashMap<String, String>)cMethodParams.get("p1");
	        	
	        	if ( null == cParams )
	 	        {
	 	        	cRes = false;
	 	        	
	 	        	LOG.error(cMethodName + "::params is null. "
	 	        							  + "Check HashMap<String, String> params parameter!");
	 	        }
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cAmazonClientWorker = new AmazonClientWorker(
	        			this.cSystemConfiguration, 
	        			this.cWorkerConfiguration);
	        	
	        	cItemSearchResults = cAmazonClientWorker.itemSearchList(cParams);
	        }
	        
	        //--------
	        if ( cRes )
	        {
	        	cMethodResults.put("r1", cItemSearchResults);
	        }
	        
	        LOG.info("------------------");
	        
	        return cRes;
    	}
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
	}
}
