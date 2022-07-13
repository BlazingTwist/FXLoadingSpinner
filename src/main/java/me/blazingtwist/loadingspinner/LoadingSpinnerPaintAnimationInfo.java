package me.blazingtwist.loadingspinner;

import javafx.beans.NamedArg;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * Fields:
 * <p>{@link LoadingSpinnerPaintAnimationInfo#paint}</p>
 * <p>{@link LoadingSpinnerPaintAnimationInfo#blendInDuration}</p>
 * <p>{@link LoadingSpinnerPaintAnimationInfo#holdDuration}</p>
 * <p>{@link LoadingSpinnerPaintAnimationInfo#blendOutDuration}</p>
 */
public class LoadingSpinnerPaintAnimationInfo {

	/**
	 * <p>Specifies the paint for the loading bar.</p>
	 * <p>Default is color '#4285f4'</p>
	 */
	private final Paint paint;

	/**
	 * <p>Specifies the duration until this paint is fully faded in.</p>
	 * <p>Ignored if only one paint info is provided.</p>
	 * <p>The blendOutDuration of the previous paint info is added to this duration.</p>
	 * <p>default is 250 milliseconds</p>
	 */
	private final Duration blendInDuration;

	/**
	 * <p>Specifies the duration until this paint starts fading out.</p>
	 * <p>Ignored if only one paint info is provided.</p>
	 * <p>default is 900 milliseconds</p>
	 */
	private final Duration holdDuration;

	/**
	 * <p>Specifies the duration until this paint is fully faded out.</p>
	 * <p>Ignored if only one paint info is provided.</p>
	 * <p>The blendInDuration of the next paint info is added to this duration.</p>
	 * <p>default is 250 milliseconds</p>
	 */
	private final Duration blendOutDuration;

	/**
	 * @param paint            {@link LoadingSpinnerPaintAnimationInfo#paint see field javadoc}
	 * @param blendInDuration  {@link LoadingSpinnerPaintAnimationInfo#blendInDuration see field javadoc}
	 * @param holdDuration     {@link LoadingSpinnerPaintAnimationInfo#holdDuration see field javadoc}
	 * @param blendOutDuration {@link LoadingSpinnerPaintAnimationInfo#blendOutDuration see field javadoc}
	 */
	public LoadingSpinnerPaintAnimationInfo(
			@NamedArg("paint") Paint paint,
			@NamedArg("blendInDuration") Duration blendInDuration,
			@NamedArg("holdDuration") Duration holdDuration,
			@NamedArg("blendOutDuration") Duration blendOutDuration) {
		this.paint = paint != null ? paint : Paint.valueOf("#4285f4");
		this.blendInDuration = blendInDuration != null ? blendInDuration : Duration.millis(250);
		this.holdDuration = holdDuration != null ? holdDuration : Duration.millis(900);
		this.blendOutDuration = blendOutDuration != null ? blendOutDuration : Duration.millis(250);
	}

	/**
	 * {@link LoadingSpinnerPaintAnimationInfo#paint see field javadoc}
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * {@link LoadingSpinnerPaintAnimationInfo#blendInDuration see field javadoc}
	 */
	public Duration getBlendInDuration() {
		return blendInDuration;
	}

	/**
	 * {@link LoadingSpinnerPaintAnimationInfo#holdDuration see field javadoc}
	 */
	public Duration getHoldDuration() {
		return holdDuration;
	}

	/**
	 * {@link LoadingSpinnerPaintAnimationInfo#blendOutDuration see field javadoc}
	 */
	public Duration getBlendOutDuration() {
		return blendOutDuration;
	}

	@Override
	public String toString() {
		return "LoadingSpinnerPaintAnimationInfo{"
				+ "paint: " + paint
				+ ", blendInDuration: " + blendInDuration
				+ ", holdDuration: " + holdDuration
				+ ", blendOutDuration: " + blendOutDuration
				+ '}';
	}
}
