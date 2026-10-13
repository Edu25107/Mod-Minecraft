package shield.modid.client.render;

import java.util.Objects;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/**
 * Renderizador de escudo personalizado que usa una textura propia por item
 * (en lugar de las texturas fijas del renderizador vanilla de escudo).
 *
 * El tipo de modelo especial se registra en {@code SpecialModelRenderers.ID_MAPPER}
 * con el id {@code enchantshield-mod:custom_shield}.
 */
public class CustomShieldSpecialRenderer implements SpecialModelRenderer<DataComponentMap> {

    private final SpriteGetter sprites;
    private final ShieldModel model;
    private final SpriteId baseTexture;

    public CustomShieldSpecialRenderer(SpriteGetter sprites, ShieldModel model, SpriteId baseTexture) {
        this.sprites = sprites;
        this.model = model;
        this.baseTexture = baseTexture;
    }

    @Override
    public @Nullable DataComponentMap extractArgument(ItemStack stack) {
        return stack.immutableComponents();
    }

    @Override
    public void submit(
        final @Nullable DataComponentMap components,
        final PoseStack poseStack,
        final SubmitNodeCollector submitNodeCollector,
        final int lightCoords,
        final int overlayCoords,
        final boolean hasFoil,
        final int outlineColor
    ) {
        BannerPatternLayers patterns = components != null
            ? components.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
            : BannerPatternLayers.EMPTY;
        DyeColor baseColor = components != null ? components.get(DataComponents.BASE_COLOR) : null;
        boolean hasPatterns = !patterns.layers().isEmpty() || baseColor != null;

        if (hasFoil && !hasPatterns) {
            submitNodeCollector.submitModel(
                this.model,
                Unit.INSTANCE,
                poseStack,
                RenderTypes.entitySolidGlint(this.baseTexture.atlasLocation()),
                lightCoords,
                overlayCoords,
                -1,
                this.sprites.get(this.baseTexture),
                outlineColor
            );
        } else {
            submitNodeCollector.submitModel(
                this.model,
                Unit.INSTANCE,
                poseStack,
                lightCoords,
                overlayCoords,
                -1,
                this.baseTexture,
                this.sprites,
                outlineColor
            );
        }

        if (hasPatterns) {
            BannerRenderer.submitPatterns(
                this.sprites,
                poseStack,
                submitNodeCollector,
                lightCoords,
                overlayCoords,
                this.model,
                Unit.INSTANCE,
                false,
                Objects.requireNonNullElse(baseColor, DyeColor.WHITE),
                patterns
            );
            if (hasFoil) {
                submitNodeCollector.order(patterns.layers().size() + 2)
                    .submitModel(
                        this.model,
                        Unit.INSTANCE,
                        poseStack,
                        RenderTypes.patternedShieldGlint(),
                        lightCoords,
                        overlayCoords,
                        -1,
                        this.sprites.get(this.baseTexture),
                        0
                    );
            }
        }
    }

    @Override
    public void getExtents(final Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked<DataComponentMap> {
        public static final MapCodec<CustomShieldSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Identifier.CODEC.fieldOf("texture").forGetter(CustomShieldSpecialRenderer.Unbaked::texture)
            ).apply(instance, CustomShieldSpecialRenderer.Unbaked::new)
        );

        @Override
        public MapCodec<CustomShieldSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public CustomShieldSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            SpriteId sprite = Sheets.SHIELD_MAPPER.apply(this.texture);
            return new CustomShieldSpecialRenderer(
                context.sprites(),
                new ShieldModel(context.entityModelSet().bakeLayer(ModelLayers.SHIELD)),
                sprite
            );
        }
    }
}
