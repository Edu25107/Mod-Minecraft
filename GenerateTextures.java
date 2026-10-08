import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenerateTextures {
    // Colores para cada escudo (RGB)
    private static final Map<String, Color> SHIELD_COLORS = new HashMap<>();
    static {
        SHIELD_COLORS.put("enchantable_shield", new Color(255, 60, 60));    // Rojo
        SHIELD_COLORS.put("reinforced_shield", new Color(30, 30, 30));       // Negro
        SHIELD_COLORS.put("mystical_shield", new Color(60, 120, 255));       // Azul
        SHIELD_COLORS.put("wooden_shield", new Color(255, 220, 60));         // Amarillo
    }

    private static final int SIZE = 64;

    public static void main(String[] args) throws IOException {
        String outputDir = "src/main/resources/assets/enchantshield-mod/textures/item";
        new File(outputDir).mkdirs();

        // Crear borde compartido
        BufferedImage border = createBorder();
        ImageIO.write(border, "PNG", new File(outputDir + "/shield_border.png"));

        // Crear cada escudo
        for (Map.Entry<String, Color> entry : SHIELD_COLORS.entrySet()) {
            String name = entry.getKey();
            Color rgb = entry.getValue();

            BufferedImage base = createShieldBase(rgb);
            BufferedImage finalImg = combineImages(base, border);

            File outputFile = new File(outputDir + "/" + name + ".png");
            ImageIO.write(finalImg, "PNG", outputFile);
            System.out.println("Creado: " + outputFile.getAbsolutePath());
        }

        System.out.println("\n¡Texturas generadas en " + outputDir + "!");
    }

    private static BufferedImage createShieldBase(Color color) {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Forma del escudo vanilla (kite shape)
        int[] xPoints = {32, 58, 60, 32, 4, 6};
        int[] yPoints = {4, 20, 52, 60, 52, 20};
        int nPoints = 6;

        // Relleno principal
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, nPoints);

        // Borde más oscuro
        Color borderColor = new Color(
            Math.max(0, color.getRed() - 60),
            Math.max(0, color.getGreen() - 60),
            Math.max(0, color.getBlue() - 60),
            255
        );
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xPoints, yPoints, nPoints);

        // Detalle central (línea vertical)
        Color highlightColor = new Color(
            Math.min(255, color.getRed() + 40),
            Math.min(255, color.getGreen() + 40),
            Math.min(255, color.getBlue() + 40),
            255
        );
        g.setColor(highlightColor);
        g.setStroke(new BasicStroke(2));
        g.drawLine(32, 10, 32, 54);

        // Detalle horizontal
        g.setStroke(new BasicStroke(1));
        g.drawLine(18, 32, 46, 32);

        g.dispose();
        return img;
    }

    private static BufferedImage createBorder() {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int[] xPoints = {32, 58, 60, 32, 4, 6};
        int[] yPoints = {4, 20, 52, 60, 52, 20};

        // Borde metálico
        g.setColor(new Color(120, 120, 120, 255));
        g.setStroke(new BasicStroke(3));
        g.drawPolygon(xPoints, yPoints, 6);

        g.dispose();
        return img;
    }

    private static BufferedImage combineImages(BufferedImage base, BufferedImage overlay) {
        BufferedImage combined = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(base, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);
        g.dispose();
        return combined;
    }
}