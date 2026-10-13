import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Genera las texturas de los escudos en el layout UV correcto del modelo vanilla
 * (ShieldModel: placa texOffs(0,0) de 12x22x1 sobre un canvas 64x64).
 *
 * Regiones UV del modelo (canvas 64x64):
 *   - Cara Norte (trasera): (1,1)-(13,23)
 *   - Cara Sur  (frontal, la que se ve en el inventario): (14,1)-(26,23)
 *   - Cantos de la placa: columna x=0..1 (Este) y x=13..14 (Oeste, incluye arriba/abajo)
 *   - Mango (handle): (26,0)-(42,12)
 *
 * El arte del kite (diseño original del mod) se dibuja escaleado a las regiones
 * norte y sur; si se dibujara en TODO el canvas como antes, el modelo solo
 * "vería" el cuadrante superior-izquierdo y el escudo aparecería pequeño/cortado.
 */
public class GenerateShieldTexturesV2 {

    // Colores para cada escudo (RGB)
    private static final Map<String, Color> SHIELD_COLORS = new HashMap<>();
    static {
        SHIELD_COLORS.put("enchantable_shield", new Color(255, 60, 60));    // Rojo
        SHIELD_COLORS.put("reinforced_shield", new Color(70, 70, 90));      // Negro/gris oscuro
        SHIELD_COLORS.put("mystical_shield", new Color(60, 120, 255));      // Azul
        SHIELD_COLORS.put("wooden_shield", new Color(180, 130, 40));        // Madera
    }

    private static final int SIZE = 64;

    public static void main(String[] args) throws IOException {
        File entityDir = new File("src/main/resources/assets/enchantshield-mod/textures/entity/shield");
        File itemDir = new File("src/main/resources/assets/enchantshield-mod/textures/item");
        entityDir.mkdirs();
        itemDir.mkdirs();

        for (Map.Entry<String, Color> entry : SHIELD_COLORS.entrySet()) {
            String name = entry.getKey();
            Color color = entry.getValue();

            BufferedImage img = createShieldTexture(color);

            File entityOut = new File(entityDir, name + "_base.png");
            ImageIO.write(img, "PNG", entityOut);
            System.out.println("Creado (entity base): " + entityOut.getAbsolutePath());

            // La textura 2D de item se usa solo para partículas; se deja igual que la base.
            File itemOut = new File(itemDir, name + ".png");
            ImageIO.write(img, "PNG", itemOut);
            System.out.println("Creado (item): " + itemOut.getAbsolutePath());
        }

        System.out.println("\n¡Texturas regeneradas en layout UV vanilla (64x64)!");
    }

    private static BufferedImage createShieldTexture(Color color) {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Colores derivados del color base
        Color borderColor = darker(color, 60);
        Color highlightColor = brighter(color, 60);
        Color handleColor = new Color(80, 80, 90, 255);

        // Kite ajustado a una caja de 12 (ancho) x 22 (alto), igual que la cara del modelo.
        // Mismo polígono/morfología que el generador original pero normalizado a la región UV.
        // Se dibuja con volteo vertical (ver drawKite): el arte se da la vuelta dentro
        // de la región 1..23 (punta del escudo abajo), como el escudo vanilla.

        // Cara Norte (trasera): región (1,1)
        drawKite(g, 1, 1, color, borderColor, highlightColor);
        // Cara Sur (frontal, visible en inventario): región (14,1)
        drawKite(g, 14, 1, color, borderColor, highlightColor);

        // Cantos laterales de la placa (1px): columna x=0 (Este) y x=13 (Oeste)
        g.setColor(borderColor);
        g.fillRect(0, 2, 1, 22);   // Este: (0,2)-(1,24)
        g.fillRect(13, 1, 1, 23);  // Oeste (+ U/D): (13,1)-(14,24)

        // Mango (handle): caja texOffs(26,0), 2x6x6 -> regiones (26..42, 0..12)
        g.setColor(handleColor);
        g.fillRect(26, 6, 16, 6);  // cubre aprox. todas las caras del mango

        g.dispose();
        return img;
    }

    /** Dibuja el kite (relleno + borde + detalle central) en la región (ox, oy),
     *  con volteo vertical: el arte se da la vuelta dentro de las filas 1..23 de la
     *  región (si se voltea el canvas entero, el arte quedaría fuera de la zona UV
     *  que el modelo muestrea y el escudo volvería a verse cortado). */
    private static void drawKite(Graphics2D g, int ox, int oy, Color fill, Color border, Color highlight) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(ox, oy + 24);
        g2.scale(1.0, -1.0);

        int n = 6;
        int[] xs = {6, 11, 12, 6, 0, 1};
        int[] ys = {1, 7, 20, 23, 20, 7};

        // Relleno principal
        g2.setColor(fill);
        g2.fillPolygon(xs, ys, n);

        // Borde más oscuro
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawPolygon(xs, ys, n);

        // Detalle central (línea vertical) y horizontal
        g2.setColor(highlight);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(6, 3, 6, 21);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(2, 7, 10, 7);
        g2.drawLine(2, 20, 10, 20);

        g2.dispose();
    }

    private static Color darker(Color c, int delta) {
        return new Color(
            Math.max(0, c.getRed() - delta),
            Math.max(0, c.getGreen() - delta),
            Math.max(0, c.getBlue() - delta),
            255
        );
    }

    private static Color brighter(Color c, int delta) {
        return new Color(
            Math.min(255, c.getRed() + delta),
            Math.min(255, c.getGreen() + delta),
            Math.min(255, c.getBlue() + delta),
            255
        );
    }
}