/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.l2client.grassmodule;

import com.jme3.gde.core.sceneexplorer.nodes.actions.AbstractNewSpatialWizardAction;
import com.jme3.gde.core.sceneexplorer.nodes.actions.NewSpatialAction;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.util.GrassLayerUtil;
import java.awt.Component;
import java.awt.Dialog;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
// @ActionID(category="...", id="com.l2client.grassmodule.AddGrassLayerWizardAction")
// @ActionRegistration(displayName="Open AddGrassLayer Wizard")
// @ActionReference(path="Menu/Tools", position=...)
@org.openide.util.lookup.ServiceProvider(service = NewSpatialAction.class)
public final class AddGrassLayerWizardAction extends AbstractNewSpatialWizardAction {
    
        public AddGrassLayerWizardAction() {
        name = "Grass..";
    }

    @Override
    protected Object showWizard(org.openide.nodes.Node node) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new AddGrassLayerWizardPanel1(pm));
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Add Grass");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wiz);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wiz.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            return wiz;
        }
        return null;
    }

    @Override
    protected com.jme3.scene.Spatial doCreateSpatial(com.jme3.scene.Node node, Object properties) {
        if (properties != null) {
            return generateGrass(node, (WizardDescriptor) properties);
        }
        return null;
    }

    private Spatial generateGrass(Node node, WizardDescriptor wiz) {
            String texture = (String) wiz.getProperty("texture");
            float scale = (Float) wiz.getProperty("scale");
            float width = (Float) wiz.getProperty("width");
            float height = (Float) wiz.getProperty("height");
            float spread = (Float) wiz.getProperty("spread");
            float fadeEnd = (Float) wiz.getProperty("fadeEnd");
            float fadeRange = (Float) wiz.getProperty("fadeRange");
            float channelMinIntensity = (Float) wiz.getProperty("channelMinIntensity");
            int channelId = (Integer) wiz.getProperty("channelId");
            int clusters = (Integer) wiz.getProperty("clusters");
            
            return GrassLayerUtil.createPatchField(
                    node, pm, texture, scale, width, 
                    height, spread, fadeEnd, fadeRange, 
                    channelMinIntensity, channelId, clusters);
    }
}
