/**
 * Copyright [2015] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jpa.modeler.navigator.entitygraph;

import org.netbeans.jpa.modeler.navigator.entitygraph.component.spec.EGChildNode;
import org.netbeans.jpa.modeler.navigator.entitygraph.component.spec.EGNode;
import org.netbeans.jpa.modeler.navigator.entitygraph.component.spec.EGParentNode;
import org.openide.explorer.view.CheckableNode;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class CheckableAttributeNode implements CheckableNode {

    private EGNode node;
    private boolean checked = false;
    private boolean checkEnabled = true;
    private boolean enableWithParent = false;
    

    public CheckableAttributeNode(boolean checked) {
        this.checked= checked;
    }
    public CheckableAttributeNode() {
    }
    
    /**
     * Tell the view to display a check-box for this node.
     *
     * @return <code>true</code> if the check-box should be displayed,
     * <code>false</code> otherwise.
     */
    @Override
    public boolean isCheckable() {
        return true;
    }

    /**
     * Provide the enabled state of the check-box.
     *
     * @return <code>true</code> if the check-box should be enabled,
     * <code>false</code> otherwise.
     */
    @Override
    public boolean isCheckEnabled() {
        return checkEnabled;
    }

    /**
     * Provide the selected state of the check-box.
     *
     * @return <code>true</code> if the check-box should be selected,
     * <code>false</code> if it should be unselected and <code>null</code> if
     * the state is unknown.
     */
    @Override
    public Boolean isSelected() {
        return checked;//true;//data.isSelected();
    }

    /**
     * Called by the view when the check-box gets selected/unselected
     *
     * @param selected <code>true</code> if the check-box was selected,
     * <code>false</code> if the check-box was unselected.
     */
    @Override
    public void setSelected(Boolean selected) {
        checked = selected;

        if (selected) {     //For parent and child_pk
            CheckableAttributeNode parentCheckableNode = null;
            if (node instanceof EGChildNode) {  //for parent
                parentCheckableNode = ((EGChildNode)node).getParent().getCheckableNode();
                if (parentCheckableNode != null && !parentCheckableNode.isSelected()) {
                    parentCheckableNode.setSelected(Boolean.TRUE);
                } 
            }
            
            if (node instanceof EGParentNode) {//For child_pk
                for (EGChildNode childNode : ((EGParentNode) node).getChildList()) {
                    if (childNode.getCheckableNode().isEnableWithParent()) {
                        childNode.getCheckableNode().setSelected(true);
                    }
                }
            }
            
        } else { //if unselected
            //For children : uncheck the child if parent unchecked
            if (!selected && node instanceof EGParentNode) {
                for (EGChildNode childNode : ((EGParentNode) node).getChildList()) {
                    if (childNode.getCheckableNode().isSelected()) {
                        childNode.getCheckableNode().setSelected(false);
                    }
                }
            }
        }
        
        if(node!=null){
        node.refreshView();
        }
    }

    /**
     * @return the node
     */
    public EGNode getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(EGNode node) {
        this.node = node;
    }

    /**
     * @param checkEnabled the checkEnabled to set
     */
    public void setCheckEnabled(boolean checkEnabled) {
        this.checkEnabled = checkEnabled;
    }

    /**
     * @return the enableWithParent
     */
    public boolean isEnableWithParent() {
        return enableWithParent;
    }

    /**
     * @param enableWithParent the enableWithParent to set
     */
    public void setEnableWithParent(boolean enableWithParent) {
        this.enableWithParent = enableWithParent;
        
        
        if (enableWithParent && node instanceof EGChildNode) {  //for parent
            CheckableAttributeNode parentCheckableNode = ((EGChildNode) node).getParent().getCheckableNode();
            if (parentCheckableNode != null && parentCheckableNode.isSelected()) {
                this.setSelected(true);
            } else {
                this.setSelected(false);
            }
        }
    }
}
