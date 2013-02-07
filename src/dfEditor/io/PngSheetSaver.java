package dfEditor.io;

import dfEditor.animation.Animation;
import dfEditor.animation.AnimationCell;
import dfEditor.animation.ExportRowsColumnsDialog;
import dfEditor.dfEditorApp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 2/6/13 4:42 PM
 */
public class PngSheetSaver {
    public static void save(String filePath, Animation animation) throws IOException {

        Rectangle cellRect = getCellRectangle(animation);

        Dimension dim = findMultipliers(animation.numCells());
        int rows = dim.height;
        int columns = dim.width;

        ExportRowsColumnsDialog.ExportDialogResult result = ExportRowsColumnsDialog.showDialog(
                dfEditorApp.getApplication().getMainFrame(),
                animation.numCells(),
                rows,
                columns,
                cellRect.width,
                cellRect.height
        );

        if (result.cancelled ) {
            return;
        } else {
            rows = result.rows;
            columns = result.columns;
        }

        int totalWidth = cellRect.width * columns;
        int totalHeight = cellRect.height * rows;

        BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics ig = image.getGraphics();

        for (int i=0; i<animation.numCells(); i++)
        {
            int rowNum = i / columns;
            int colNum = i % columns;

            AnimationCell cell = animation.getCellAtIndex(i);
            cell.rebuild();

            Rectangle r = cell.getSpreadRect();
            r.x -= cellRect.x;
            r.y -= cellRect.y;

            r.x += cellRect.width * colNum;
            r.y += cellRect.height * rowNum;

            cell.draw(ig, r);
        }

        ImageIO.write(image, CustomFilter.EXT_PNG, new File(filePath));
    }

    private static Dimension findMultipliers(int cells) {
        java.util.List<Dimension> pairs = new ArrayList<Dimension>();
        for (int i=1; i<cells; i++) {
            if (cells % i == 0) {
                pairs.add(new Dimension(i, cells/i));
            }
        }

        Dimension minDiffDim = null;
        int diff = cells;

        for (Dimension dim: pairs) {
            if ( Math.abs(dim.width - dim.height) < diff) {
                minDiffDim = dim;
                diff = Math.abs(dim.width - dim.height);
            }
        }
        return minDiffDim;
    }

    private static Rectangle getCellRectangle(Animation animation) {
        Rectangle firstCellRect = animation.getCellAtIndex(0).getSpreadRect();
        Point topLeft = new Point(firstCellRect.x, firstCellRect.y);
        Point bottomRight = new Point(firstCellRect.x + firstCellRect.width, firstCellRect.y + firstCellRect.height);

        for (int i=1; i<animation.numCells(); i++)
        {
            Rectangle r = animation.getCellAtIndex(i).getSpreadRect();

            if (r.x < topLeft.x)
                topLeft.x = r.x;
            if (r.y < topLeft.y)
                topLeft.y = r.y;
            if (r.x + r.width > bottomRight.x)
                bottomRight.x = r.x + r.width;
            if (r.y + r.height > bottomRight.y)
                bottomRight.y = r.y + r.height;
        }
        return new Rectangle(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
    }
}
