/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.core.widget.table;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.spec.DBMapping;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.DBModelerUtil;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.BASE_TABLE;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.BASE_TABLE_ICON_PATH;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.SecondaryTable;
import org.netbeans.jpa.modeler.spec.Table;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class BaseTableWidget extends TableWidget<DBTable> {

    public BaseTableWidget(DBModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (String value) -> {
            setName(value);
            setLabel(name);
        });
    }
    
    @Override
    public void init() {
        super.init();
        Table table = this.getBaseElementSpec().getEntity().getTable(this.getName());
        if(table instanceof SecondaryTable){
            this.setImage(DBModelerUtil.SECONDARY_TABLE);
        }
    }

    private void setDefaultName() {
        Entity entity = this.getBaseElementSpec().getEntity();
        this.name = entity.getDefaultTableName();
        entity.getTable().setName(null);
        setLabel(name);
    }

    @Override
    public void setName(String name) {

        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                Entity entity = this.getBaseElementSpec().getEntity();
                entity.getTable().setName(this.name);
            }
        } else {
            setDefaultName();
        }

        if (SQLKeywords.isSQL99ReservedKeyword(BaseTableWidget.this.getName())) {
            this.getErrorHandler().throwError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getErrorHandler().clearError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBMapping mapping = BaseTableWidget.this.getModelerScene().getBaseElementSpec();
        if (mapping.findAllTable(BaseTableWidget.this.getName()).size() > 1) {
            getErrorHandler().throwError(EntityValidator.NON_UNIQUE_TABLE_NAME);
        } else {
            getErrorHandler().clearError(EntityValidator.NON_UNIQUE_TABLE_NAME);
        }

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        Entity entity = this.getBaseElementSpec().getEntity();
        Table table = entity.getTable(this.getName());
        if (table == null) {
            table = entity.getTable();
        }
        set.createPropertySet(this, table, getPropertyChangeListeners());
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        JMenuItem joinTable = new JMenuItem("Create Secondary Table");
        joinTable.addActionListener((ActionEvent e) -> {
            Entity entity = this.getBaseElementSpec().getEntity();
            String secondaryTableName = JOptionPane.showInputDialog((Component) BaseTableWidget.this.getModelerScene().getModelerPanelTopComponent(), "Please enter secondary table name");
            if (entity.getTable(secondaryTableName) == null) { //check from complete table list
                SecondaryTable secondaryTable = new SecondaryTable();
                secondaryTable.setName(secondaryTableName);
                entity.addSecondaryTable(secondaryTable);
                ModelerFile parentFile = BaseTableWidget.this.getModelerScene().getModelerFile().getParentFile();
                JPAModelerUtil.openDBViewer(parentFile, (EntityMappings) parentFile.getModelerScene().getBaseElementSpec());
            } else {
                JOptionPane.showMessageDialog((Component) BaseTableWidget.this.getModelerScene().getModelerPanelTopComponent(),
                        "Table already exist");
            }
        });
        menuList.add(0, joinTable);
        return menuList;
    }

    @Override
    public String getIconPath() {
        return BASE_TABLE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return BASE_TABLE;
    }

}
