#!/usr/bin/env python3
"""
Genera texturas de escudos basadas en el vanilla shield base.png
pero coloreadas: rojo, negro, azul, amarillo.
"""
from PIL import Image, ImageDraw
import os

# Colores para cada escudo (RGB)
SHIELD_COLORS = {
    "enchantable_shield": (255, 60, 60),    # Rojo
    "reinforced_shield": (30, 30, 30),       # Negro
    "mystical_shield": (60, 120, 255),       # Azul
    "wooden_shield": (255, 220, 60),         # Amarillo
}

# Tamaño vanilla del shield base: 64x64
SIZE = (64, 64)

def create_shield_base(color_name, rgb):
    """Crea una textura de escudo base con forma de escudo vanilla."""
    img = Image.new('RGBA', SIZE, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Forma aproximada del escudo vanilla (basada en base.png)
    # El escudo vanilla tiene forma de "kite" / cometa
    shield_points = [
        (32, 4),    # Punta superior
        (58, 20),   # Esquina sup derecha
        (60, 52),   # Esquina inf derecha
        (32, 60),   # Punta inferior
        (4, 52),    # Esquina inf izquierda
        (6, 20),    # Esquina sup izquierda
    ]
    
    # Dibujar forma principal del escudo
    draw.polygon(shield_points, fill=rgb + (255,))
    
    # Borde más oscuro
    border_color = tuple(max(0, c - 60) for c in rgb) + (255,)
    draw.polygon(shield_points, outline=border_color, width=2)
    
    # Detalle central (línea vertical como vanilla)
    center_color = tuple(min(255, c + 40) for c in rgb) + (255,)
    draw.line([(32, 10), (32, 54)], fill=center_color, width=2)
    
    # Detalle horizontal
    draw.line([(18, 32), (46, 32)], fill=center_color, width=1)
    
    return img

def create_shield_border():
    """Crea el borde del escudo (igual para todos)."""
    img = Image.new('RGBA', SIZE, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    shield_points = [
        (32, 4), (58, 20), (60, 52), (32, 60), (4, 52), (6, 20)
    ]
    
    # Borde metálico
    draw.polygon(shield_points, outline=(120, 120, 120, 255), width=3)
    
    return img

def main():
    output_dir = r"C:\Users\videojuegos\Desktop\enchantshield-mod-template-26.3\src\main\resources\assets\enchantshield-mod\textures\item"
    os.makedirs(output_dir, exist_ok=True)
    
    # Crear borde compartido
    border = create_shield_border()
    border.save(os.path.join(output_dir, "shield_border.png"))
    
    # Crear cada escudo
    for name, rgb in SHIELD_COLORS.items():
        base = create_shield_base(name, rgb)
        
        # Combinar base + borde
        final = Image.alpha_composite(base.convert('RGBA'), border.convert('RGBA'))
        
        output_path = os.path.join(output_dir, f"{name}.png")
        final.save(output_path)
        print(f"Creado: {output_path}")
    
    print("\n¡Texturas generadas!")
    print(f"Directorio: {output_dir}")

if __name__ == "__main__":
    main()