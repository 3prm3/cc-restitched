/*
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */
package dan200.computercraft.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3f;
import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A model to render, combined with a transformation matrix to apply.
 */
@Environment( EnvType.CLIENT )
public final class TransformedModel
{
    private final BakedModel model;
    private final AffineTransformation matrix;

    public TransformedModel( @Nonnull BakedModel model, @Nonnull AffineTransformation matrix )
    {
        this.model = Objects.requireNonNull( model );
        this.matrix = Objects.requireNonNull( matrix );
    }

    public TransformedModel( @Nonnull BakedModel model )
    {
        this.model = Objects.requireNonNull( model );
        matrix = AffineTransformation.identity();
    }

    public static TransformedModel of( @Nonnull ModelIdentifier location )
    {
        BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
        return new TransformedModel( modelManager.getModel( location ) );
    }

    public static TransformedModel of( @Nonnull ItemStack item, @Nonnull AffineTransformation transform )
    {
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModels().getModel( item );
        return new TransformedModel( model, transform );
    }

    @Nonnull
    public BakedModel getModel()
    {
        return model;
    }

    @Nonnull
    public AffineTransformation getMatrix()
    {
        return matrix;
    }

    public void push( MatrixStack matrixStack )
    {
        matrixStack.push();

        Vec3f translation = matrix.getTranslation();
        matrixStack.translate( translation.getX(), translation.getY(), translation.getZ() );

        matrixStack.multiply( matrix.getRotation2() );

        Vec3f scale = matrix.getScale();
        matrixStack.scale( scale.getX(), scale.getY(), scale.getZ() );

        matrixStack.multiply( matrix.getRotation1() );
    }
}
