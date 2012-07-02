package com.l2client.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/** 
 * A drag and drop action identified only by its id, lookup should be performed by others
 *
 */
public class TransferableAction implements Transferable {
    final static public DataFlavor TRANSFER_FLAVOR = new DataFlavor( TransferableAction.class, "Image" );
    static DataFlavor flavors[] = {TRANSFER_FLAVOR};
    private int actionID;

    public TransferableAction(int id) {
        this.actionID = id;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        return flavor.equals( TRANSFER_FLAVOR );
    }

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return actionID;
	}

}
