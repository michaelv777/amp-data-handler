/**
 * 
 */
package com.amp.data.handler.base;

import java.util.List;
import java.util.Map;

import com.amp.jpa.entities.Category;
import com.amp.jpa.entities.Node;

/**
 * @author MVEKSLER
 *
 */
public interface DataHandlerI 
{
	public boolean handleBrowseNodesHierarchy();
	
	public boolean handleNodeLookup(
		  	Map<String, Object> cMethodParams,
		  	Map<String, Object> cMethodResults);
	
	public boolean getNodeLookup(
		  	Map<String, Object> cMethodParams,
		  	Map<String, Object> cMethodResults);

	public boolean saveNodeLookup(
		  	Map<String, Object> cMethodParams,
		  	Map<String, Object> cMethodResults);
	
	public boolean handleCategoryNodeLookup(
		  	Map<String, Object> cMethodParams,
		  	Map<String, Object> cMethodResults);
	
	public boolean getCategoryNodeLookup(
		  	Map<String, Object> cMethodParams,
		  	Map<String, Object> cMethodResults);

	public boolean handleCategoryNode(
			Map<String, Object> cMethodParams,
			Map<String, Object> cMethodResults);
	
	public boolean saveCategoryNode(
			Map<String, Object> cMethodParams,
			Map<String, Object> cMethodResults);
	
	public boolean saveCategoryNodeLookup(
		  	Map<String, Object> cMethodParams,
		  	Map<String, Object> cMethodResults);
	
	public boolean getRootCategories(
			Map<String, Object> cMethodParams,
			Map<String, Object> cMethodResults);
	
	public boolean getRootCategoriesNodes(
			Map<String, Object> cMethodParams,
			Map<String, Object> cMethodResults);

	public boolean getRootCategorySubNodes(
			Map<String, Object> cMethodParams,
	 	     Map<String, Object> cMethodResults);
	
	public List<Category> getRootCategories(
			String cSourceName);
	
	public List<Node> getRootCategoriesNodes(
			String cSourceName);
	
	public Category getRootCategory(
			String cSourceName, 
			String cCategoryBrowseNodeId);
	
	//--------------------------------------
	public boolean handleItemSearchList(
			Map<String, Object> cMethodParams,
			Map<String, Object> cMethodResults);
	
	public boolean getItemSearchList(
			Map<String, Object> cMethodParams,
			Map<String, Object> cMethodResults);
}
