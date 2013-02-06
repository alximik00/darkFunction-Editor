package dfEditor.io;

import dfEditor.animation.Animation;
import dfEditor.animation.AnimationCell;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 2/6/13 4:42 PM
 */
public class PngSheetSaver {
    public static void save(String filePath, Animation animation) throws IOException {

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

        int cellWidth = bottomRight.x - topLeft.x;
        int cellHeight = bottomRight.y - topLeft.y;

        int totalWidth = cellWidth * animation.numCells();
        int totalHeight = cellHeight;

        BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics ig = image.getGraphics();

        for (int i=0; i<animation.numCells(); i++)
        {
            AnimationCell cell = animation.getCellAtIndex(i);
            cell.rebuild();

            Rectangle r = cell.getSpreadRect();
            r.x -= topLeft.x;
            r.y -= topLeft.y;

            r.x += cellWidth;
            cell.draw(ig, r);
        }

        ImageIO.write(image, CustomFilter.EXT_PNG, new File(filePath));
    }
}
