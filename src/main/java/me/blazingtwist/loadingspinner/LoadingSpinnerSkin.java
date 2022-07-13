package me.blazingtwist.loadingspinner;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LoadingSpinnerSkin extends SkinBase<Control> {

	public static final String css_styleClass_progressBar = "-ls-progress-bar";
	public static final String css_styleClass_track = "-ls-track";
	public static final String css_styleClass_progressText = "-ls-progress-text";
	public static final String css_styleClass_iconSVGPath = "-ls-icon-svg-path";

	private static final double iconAnimationMinAngleChange = 120;

	protected final LoadingSpinner control;

	private final Arc progressBar;
	private final Pane progressRotationPane;
	private final Arc track;
	private final Text progressText;
	private final SVGPath iconSVGPath;
	private final StackPane containerPane;

	protected int currentPaintIndex = 0;
	protected Timeline paintTimeline;

	protected int currentIndeterminateCycleIndex = 0;
	protected Timeline indeterminateTimeline;
	protected DoubleProperty indeterminateInflateStrength = new SimpleDoubleProperty(LoadingSpinnerSkin.this, "indeterminateInflateStrength", 0);

	protected boolean progressTextShown = true;
	protected FadeTransition progressTextFadeAnimation;

	protected LoadingSpinnerAnimatedIcon currentShowAnimIcon = null;
	protected Timeline iconAngleTimeline;
	protected Timeline iconColorTimeline;
	protected Timeline iconPathStrokeTimeline;

	protected LoadingSpinnerSkin(LoadingSpinner control) {
		super(control);
		this.control = control;

		progressBar = new Arc();
		progressBar.setManaged(false);
		progressBar.getStyleClass().add(css_styleClass_progressBar);
		progressBar.setFill(Color.TRANSPARENT);

		progressRotationPane = new Pane();
		progressRotationPane.getChildren().add(progressBar);

		track = new Arc();
		track.getStyleClass().add(css_styleClass_track);
		track.setFill(Color.TRANSPARENT);
		track.setLength(360);

		progressText = new Text();
		progressText.getStyleClass().add(css_styleClass_progressText);

		iconSVGPath = new SVGPath();
		iconSVGPath.setManaged(false);
		iconSVGPath.getStyleClass().add(css_styleClass_iconSVGPath);
		iconSVGPath.setFill(Color.TRANSPARENT);
		iconSVGPath.setStrokeLineCap(StrokeLineCap.BUTT);

		containerPane = new StackPane();
		containerPane.setAlignment(Pos.CENTER);
		containerPane.setPrefSize(64, 64);
		containerPane.getChildren().addAll(progressRotationPane, track, progressText, iconSVGPath);

		this.getChildren().setAll(containerPane);
		attachListeners(control);
	}

	private <DataType> void attachListenerAndExecute(ObservableValue<DataType> property, Runnable onChangeCallback) {
		property.addListener((observable, oldValue, newValue) -> onChangeCallback.run());
		onChangeCallback.run();
	}

	private <DataType> void attachListenerAndExecute(ObservableValue<DataType> property, Consumer<DataType> newValueConsumer) {
		property.addListener((observable, oldValue, newValue) -> newValueConsumer.accept(newValue));
		newValueConsumer.accept(property.getValue());
	}

	private <DataType> void attachListenerAndExecute(ObservableList<DataType> listProperty, Runnable onChangeCallback) {
		listProperty.addListener((ListChangeListener<DataType>) change -> onChangeCallback.run());
		onChangeCallback.run();
	}

	protected void pauseTimeline(Timeline timeline, boolean shouldPause) {
		if (timeline != null) {
			if (shouldPause) {
				timeline.pause();
			} else {
				timeline.play();
			}
		}
	}

	protected void checkControlVisibility() {
		boolean isVisible = control.getParent() != null
				&& control.getScene() != null
				&& control.isVisible();
		pauseTimeline(paintTimeline, !isVisible);
		pauseTimeline(indeterminateTimeline, !isVisible);
	}

	protected void checkIndeterminateBarLength() {
		if (control.isIndeterminate()) {
			DeflateAnimationInfo deflateInfo = DeflateAnimationInfo.computeDeflateParameters(this);
			final double inflateDifference = (deflateInfo.inflateLength - deflateInfo.deflateLength);
			final double absArcLength = deflateInfo.deflateLength + (inflateDifference * indeterminateInflateStrength.get());
			progressBar.setLength(absArcLength * deflateInfo.rotationFactor);
		}
	}

	protected void updateProgress(Number rawProgress) {
		double progress = Math.max(-1, Math.min(1, rawProgress.doubleValue()));
		progressText.setText(Math.round(Math.abs(progress) * 100) + "%");
		if (currentShowAnimIcon == null) {
			if (!control.isIndeterminate()) {
				progressBar.setLength(-360d * progress);
			} else {
				checkIndeterminateBarLength();
			}
		}
	}

	protected void clearTimeline(Timeline timeline) {
		if (timeline != null) {
			timeline.stop();
			timeline.getKeyFrames().clear();
		}
	}

	protected void clearPaintTimeline() {
		clearTimeline(paintTimeline);
		paintTimeline = null;
	}

	protected void clearIndeterminateTimeline() {
		clearTimeline(indeterminateTimeline);
		indeterminateTimeline = null;
	}

	protected void clearProgressTextFadeAnimation() {
		if (progressTextFadeAnimation != null) {
			progressTextFadeAnimation.stop();
			progressTextFadeAnimation = null;
		}
	}

	protected void clearIconAnimationTimeline() {
		clearTimeline(iconAngleTimeline);
		iconAngleTimeline = null;
	}

	protected void clearIconColorTimeline() {
		clearTimeline(iconColorTimeline);
		iconColorTimeline = null;
	}

	protected void clearIconPathStrokeTimeline() {
		clearTimeline(iconPathStrokeTimeline);
		iconPathStrokeTimeline = null;
	}

	protected void onCurrentPaintHoldAnimEnd() {
		ObservableList<LoadingSpinnerPaintAnimationInfo> paintAnimationSequence = control.getPaintAnimationSequence();
		LoadingSpinnerPaintAnimationInfo currentPaintInfo = paintAnimationSequence.get(currentPaintIndex);
		currentPaintIndex = (currentPaintIndex + 1) % paintAnimationSequence.size();
		LoadingSpinnerPaintAnimationInfo nextPaintInfo = paintAnimationSequence.get(currentPaintIndex);

		Duration blendTimestamp = currentPaintInfo.getBlendOutDuration().add(nextPaintInfo.getBlendInDuration());
		Duration holdTimestamp = nextPaintInfo.getHoldDuration().add(blendTimestamp);

		clearPaintTimeline();
		paintTimeline = new Timeline(
				new KeyFrame(blendTimestamp,
						new KeyValue(progressBar.strokeProperty(), nextPaintInfo.getPaint()),
						new KeyValue(iconSVGPath.strokeProperty(), nextPaintInfo.getPaint())
				),
				new KeyFrame(holdTimestamp,
						new KeyValue(progressBar.strokeProperty(), nextPaintInfo.getPaint()),
						new KeyValue(iconSVGPath.strokeProperty(), nextPaintInfo.getPaint())
				)
		);
		paintTimeline.setOnFinished(event -> onCurrentPaintHoldAnimEnd());
		paintTimeline.setCycleCount(1);
		paintTimeline.setDelay(Duration.ZERO);
		paintTimeline.playFromStart();
	}

	protected void onPaintAnimationSequenceChanged() {
		if (currentShowAnimIcon != null && currentShowAnimIcon.getPaint() != null) {
			return;
		}

		ObservableList<LoadingSpinnerPaintAnimationInfo> paintAnimationSequence = control.getPaintAnimationSequence();
		if (paintAnimationSequence.isEmpty()) {
			progressBar.setStroke(null);
			clearPaintTimeline();
			return;
		}

		if (paintTimeline == null) {
			currentPaintIndex = currentPaintIndex % paintAnimationSequence.size();
			LoadingSpinnerPaintAnimationInfo targetPaint = paintAnimationSequence.get(currentPaintIndex);
			Paint targetPaintValue = targetPaint.getPaint();

			Duration blendTimestamp = targetPaint.getBlendInDuration();
			Duration holdTimestamp = targetPaint.getHoldDuration().add(blendTimestamp);

			paintTimeline = new Timeline(
					new KeyFrame(blendTimestamp, new KeyValue(progressBar.strokeProperty(), targetPaintValue)),
					new KeyFrame(holdTimestamp, new KeyValue(progressBar.strokeProperty(), targetPaintValue))
			);
			paintTimeline.setOnFinished(event -> onCurrentPaintHoldAnimEnd());
			paintTimeline.setCycleCount(1);
			paintTimeline.setDelay(Duration.ZERO);
			paintTimeline.playFromStart();
		}
	}

	protected void onIndeterminateCycleEnd() {
		final int totalCycleCount = 4;
		final double angleOffsetPerCycle = (360d / totalCycleCount);
		DeflateAnimationInfo deflateInfo = DeflateAnimationInfo.computeDeflateParameters(this);
		final double rotationAnglePerSecond = 150;
		final double inflateAnglePerSecond = 540;
		// assumption: this method is *only* called when the previous cycle ended 'naturally'
		//  thus the current angle is assumed to be [angleOffsetPerCycle * (currentIndeterminateCycleIndex + 1)]
		//  and the bar length is assumed to be [deflateBarLength]

		currentIndeterminateCycleIndex = (currentIndeterminateCycleIndex + 1) % totalCycleCount;
		final double startAngle = angleOffsetPerCycle * currentIndeterminateCycleIndex;
		final double endAngle = startAngle + 360 + angleOffsetPerCycle;

		final double deflateAngleGain = (deflateInfo.inflateLength - deflateInfo.deflateLength); // deflation advances angle
		final double perStepAngleGain = ((endAngle - startAngle) - deflateAngleGain) / 4; // 4 steps in animation

		final double deflateAnimSeconds = deflateAngleGain / inflateAnglePerSecond;
		final double stepAnimSeconds = perStepAngleGain / rotationAnglePerSecond;

		final double animTimeStep1 = Math.max(deflateAnimSeconds, stepAnimSeconds);
		final double animAngleStep1 = startAngle + perStepAngleGain;
		final double animTimeStep2 = animTimeStep1 + stepAnimSeconds;
		final double animAngleStep2 = animAngleStep1 + perStepAngleGain;
		final double animTimeStep3 = animTimeStep2 + Math.max(deflateAnimSeconds, stepAnimSeconds);
		final double animAngleStep3 = animAngleStep2 + perStepAngleGain + deflateAngleGain;
		final double animTimeStep4 = animTimeStep3 + stepAnimSeconds;
		final double animAngleStep4 = animAngleStep3 + perStepAngleGain;

		clearIndeterminateTimeline();
		indeterminateTimeline = new Timeline(
				new KeyFrame(Duration.ZERO,
						new KeyValue(progressBar.startAngleProperty(), startAngle * deflateInfo.rotationFactor),
						new KeyValue(indeterminateInflateStrength, 0)
				),
				new KeyFrame(Duration.seconds(animTimeStep1),
						new KeyValue(progressBar.startAngleProperty(), animAngleStep1 * deflateInfo.rotationFactor),
						new KeyValue(indeterminateInflateStrength, 1)
				),
				new KeyFrame(Duration.seconds(animTimeStep2),
						new KeyValue(progressBar.startAngleProperty(), animAngleStep2 * deflateInfo.rotationFactor),
						new KeyValue(indeterminateInflateStrength, 1)
				),
				new KeyFrame(Duration.seconds(animTimeStep3),
						new KeyValue(progressBar.startAngleProperty(), animAngleStep3 * deflateInfo.rotationFactor),
						new KeyValue(indeterminateInflateStrength, 0)
				),
				new KeyFrame(Duration.seconds(animTimeStep4),
						new KeyValue(progressBar.startAngleProperty(), animAngleStep4 * deflateInfo.rotationFactor),
						new KeyValue(indeterminateInflateStrength, 0)
				)
		);
		indeterminateTimeline.setOnFinished(event -> onIndeterminateCycleEnd());
		indeterminateTimeline.setCycleCount(1);
		indeterminateTimeline.setDelay(Duration.ZERO);
		indeterminateTimeline.playFromStart();
	}

	protected void startIndeterminateAnimation() {
		currentIndeterminateCycleIndex = -1;
		onIndeterminateCycleEnd();
	}

	protected void animateProgressText(boolean doShowText) {
		if (doShowText == this.progressTextShown) {
			System.out.println("progtext did not change");
			return;
		}
		System.out.println("checking progtext");

		this.progressTextShown = doShowText;
		if (doShowText) { // show immediately (otherwise fade-in won't be visible)
			progressText.setVisible(true);
		}
		double currentOpacity = progressText.getOpacity();
		double targetOpacity = doShowText ? 1 : 0;
		clearProgressTextFadeAnimation();
		progressTextFadeAnimation = new FadeTransition();
		progressTextFadeAnimation.setNode(progressText);
		progressTextFadeAnimation.setFromValue(currentOpacity);
		progressTextFadeAnimation.setToValue(targetOpacity);
		progressTextFadeAnimation.setDuration(Duration.millis(Math.abs(targetOpacity - currentOpacity) * 300)); // 300ms for full opacity change, scale down linearly.

		if (!doShowText) { // hide after animation (otherwise fade-out won't be visible)
			progressTextFadeAnimation.setOnFinished(event -> progressText.setVisible(false));
		}
		progressTextFadeAnimation.setCycleCount(1);
		progressTextFadeAnimation.setDelay(Duration.ZERO);
		progressTextFadeAnimation.playFromStart();
	}

	/**
	 * Animates from the current angle to the specified target angle.
	 * Will correct for the current {@link LoadingSpinnerSkin#progressRotationPane} rotation.
	 * Occupies {@link LoadingSpinnerSkin#iconAngleTimeline} timeline.
	 *
	 * @param targetAngle          angle to rotate to
	 * @param minAngleChange       should be >= 0, minimum amount to rotate by (will add extra 360° rotations, if the direct path rotates by less than this value)
	 * @param targetLength         should be >= 0, length of the progress bar to animate to, length will be added in the direction of rotation
	 * @param animFinishedCallback callback to run after animation ends
	 * @return the duration of the animation that is started by this method
	 */
	protected Duration animateToProgressAndAngle(double targetAngle, double minAngleChange, double targetLength,
												 Duration delay, Runnable animFinishedCallback) {
		DeflateAnimationInfo deflateInfo = DeflateAnimationInfo.computeDeflateParameters(this);
		final boolean clockwise = deflateInfo.rotationFactor < 0;
		final double anglePerSecond = 630;

		delay = delay != null ? delay : Duration.ZERO;

		double normalizedAngle = progressBar.getStartAngle() % 360;
		if (normalizedAngle < 0) {
			normalizedAngle += 360;
		}

		if (clockwise) {
			// convert from [0, 360] to [-360, 0]
			normalizedAngle -= 360;
		}

		// animate in 3 steps
		// 1 - deflate to bar length 5 while increasing angle (end-position stays fixed in place)
		// 2 - spin deflated bar
		// 3 - inflate to target length after reaching target start angle
		double startLength = Math.abs(progressBar.getLength()) * deflateInfo.rotationFactor;
		double inflateLength = targetLength * deflateInfo.rotationFactor;

		double inflateAnimAngleGain = 0;
		if (targetLength < deflateInfo.deflateLength) {
			inflateAnimAngleGain = deflateInfo.deflateLength - targetLength;
		}

		double deflateLength = deflateInfo.deflateLength * deflateInfo.rotationFactor;
		double deflateDifference = Math.abs(deflateLength - startLength);
		Duration deflateDuration = Duration.seconds(deflateDifference / anglePerSecond);
		double deflateEndAngle = normalizedAngle + (deflateDifference * deflateInfo.rotationFactor);

		double extraSpin = Math.max(90, minAngleChange);
		double extraSpinEndAngle = deflateEndAngle + (extraSpin * deflateInfo.rotationFactor);
		double targetAngleGain = (targetAngle - extraSpinEndAngle - inflateAnimAngleGain - (progressRotationPane.getRotate() * deflateInfo.rotationFactor)) % 360;
		if (targetAngleGain < 0) {
			targetAngleGain += 360;
		}
		if (deflateInfo.rotationFactor < 0) {
			extraSpin += (360 - targetAngleGain);
			extraSpinEndAngle += (targetAngleGain - 360);
		} else {
			extraSpin += targetAngleGain;
			extraSpinEndAngle += targetAngleGain;
		}
		Duration extraSpinDuration = Duration.seconds(extraSpin / anglePerSecond).add(deflateDuration);

		double inflateDifference = Math.abs(targetLength - deflateInfo.deflateLength);
		Duration inflateDuration = Duration.seconds(inflateDifference / anglePerSecond).add(extraSpinDuration);
		double inflateEndAngle = extraSpinEndAngle + (inflateAnimAngleGain * deflateInfo.rotationFactor);

		clearIconAnimationTimeline();
		iconAngleTimeline = new Timeline(
				new KeyFrame(Duration.ZERO,
						new KeyValue(progressBar.lengthProperty(), startLength),
						new KeyValue(progressBar.startAngleProperty(), normalizedAngle)
				),
				new KeyFrame(deflateDuration,
						new KeyValue(progressBar.lengthProperty(), deflateLength),
						new KeyValue(progressBar.startAngleProperty(), deflateEndAngle)
				),
				new KeyFrame(extraSpinDuration,
						new KeyValue(progressBar.lengthProperty(), deflateLength),
						new KeyValue(progressBar.startAngleProperty(), extraSpinEndAngle)
				),
				new KeyFrame(inflateDuration,
						new KeyValue(progressBar.lengthProperty(), inflateLength),
						new KeyValue(progressBar.startAngleProperty(), inflateEndAngle)
				)
		);
		if (animFinishedCallback != null) {
			iconAngleTimeline.setOnFinished(event -> animFinishedCallback.run());
		}
		iconAngleTimeline.setCycleCount(1);
		iconAngleTimeline.setDelay(delay);
		iconAngleTimeline.playFromStart();
		return inflateDuration.add(delay);
	}

	protected void animateFromStaticToIcon(LoadingSpinnerAnimatedIcon targetAnimIcon) {
		currentShowAnimIcon = targetAnimIcon;
		clearIndeterminateTimeline();

		DeflateAnimationInfo deflateInfo = DeflateAnimationInfo.computeDeflateParameters(this);
		double gapAngle = (targetAnimIcon.getGapWidth() / 2) - targetAnimIcon.getGapAngle();
		if (deflateInfo.rotationFactor < 0) {
			gapAngle -= targetAnimIcon.getGapWidth();
		}
		Duration angleAnimDuration = animateToProgressAndAngle(
				gapAngle, iconAnimationMinAngleChange, 360 - targetAnimIcon.getGapWidth(), null, null
		);

		Paint paintOverride = targetAnimIcon.getPaint();
		if (paintOverride != null) {
			clearPaintTimeline();
			clearIconColorTimeline();

			iconColorTimeline = new Timeline(new KeyFrame(angleAnimDuration, new KeyValue(progressBar.strokeProperty(), paintOverride)));
			iconColorTimeline.setCycleCount(1);
			iconColorTimeline.setDelay(Duration.ZERO);
			iconColorTimeline.playFromStart();
			iconSVGPath.setStroke(paintOverride);
		}

		iconSVGPath.setContent(targetAnimIcon.getPath());
		iconSVGPath.getStrokeDashArray().setAll(targetAnimIcon.getPathLength());
		iconSVGPath.setStrokeDashOffset(targetAnimIcon.getPathLength());

		animateCurrentIcon(true, angleAnimDuration, null);
		control.requestLayout();
	}

	/**
	 * @param fadeIn if enabled, fade current icon in, otherwise fade out
	 * @param delay  start delay of animation
	 * @return the end-time of the animation (combined duration of animation and passed delay)
	 */
	protected Duration animateCurrentIcon(boolean fadeIn, Duration delay, Runnable onFinished) {
		Duration pathStrokeAnimDuration = Duration.millis(200);
		clearIconPathStrokeTimeline();

		if (fadeIn) {
			iconPathStrokeTimeline = new Timeline(
					new KeyFrame(pathStrokeAnimDuration,
							new KeyValue(iconSVGPath.strokeDashOffsetProperty(), 0)
					)
			);
		} else {
			iconSVGPath.setStrokeDashOffset(-Math.abs(iconSVGPath.getStrokeDashOffset()));
			iconPathStrokeTimeline = new Timeline(
					new KeyFrame(pathStrokeAnimDuration,
							new KeyValue(iconSVGPath.strokeDashOffsetProperty(), -currentShowAnimIcon.getPathLength())
					)
			);
		}
		iconPathStrokeTimeline.setCycleCount(1);
		iconPathStrokeTimeline.setDelay(delay);
		iconPathStrokeTimeline.setOnFinished(event -> {
			if (onFinished != null) {
				onFinished.run();
			}
		});
		iconPathStrokeTimeline.playFromStart();

		return pathStrokeAnimDuration;
	}

	protected void animateFromIconToStatic() {
		Duration iconAnimEndDuration = animateCurrentIcon(false, Duration.ZERO, () -> {
			currentShowAnimIcon = null;
			clearIconColorTimeline();
			onPaintAnimationSequenceChanged();
		});

		if (control.isIndeterminate()) {
			DeflateAnimationInfo deflateInfo = DeflateAnimationInfo.computeDeflateParameters(this);
			animateToProgressAndAngle(
					-progressRotationPane.getRotate(), 180, deflateInfo.deflateLength, iconAnimEndDuration, this::startIndeterminateAnimation
			);
		} else {
			double barLength = 360d * Math.min(1, Math.abs(control.getProgress()));
			animateToProgressAndAngle(
					-progressRotationPane.getRotate(), 180, barLength, iconAnimEndDuration, null
			);
		}
	}

	protected void animateFromIconToIcon(LoadingSpinnerAnimatedIcon newIcon) {
		// animate icon fadeOut
		// animate angle
		// animate icon fadeIn
		animateCurrentIcon(false, Duration.ZERO, () -> animateFromStaticToIcon(newIcon));
	}

	protected void checkForIconChange() {
		// verify that the iconKey still targets the same Icon
		// otherwise animate transition to icon
		LoadingSpinnerAnimatedIcon selectedAnimIcon = control.getAnimatedIcon(control.getDisplayedIcon());
		if (selectedAnimIcon == currentShowAnimIcon) {
			return;
		}

		if (currentShowAnimIcon == null) { // no icon -> icon
			animateFromStaticToIcon(selectedAnimIcon);
		} else if (selectedAnimIcon == null) { // icon -> no icon
			animateFromIconToStatic();
		} else { // icon -> new icon
			animateFromIconToIcon(selectedAnimIcon);
		}
	}

	protected void attachListeners(LoadingSpinner control) {
		attachListenerAndExecute(control.parentProperty(), this::checkControlVisibility);
		attachListenerAndExecute(control.sceneProperty(), this::checkControlVisibility);
		attachListenerAndExecute(control.visibleProperty(), this::checkControlVisibility);

		attachListenerAndExecute(indeterminateInflateStrength, this::checkIndeterminateBarLength);
		attachListenerAndExecute(control.progressProperty(), this::updateProgress);
		attachListenerAndExecute(control.progressTextProperty(), this::animateProgressText);

		attachListenerAndExecute(control.indeterminateProperty(), newValue -> {
			if (currentShowAnimIcon == null) {
				if (newValue) {
					startIndeterminateAnimation();
				} else {
					clearIndeterminateTimeline();
				}
			}
		});
		attachListenerAndExecute(control.startAngleProperty(), newValue -> progressRotationPane.setRotate(newValue.doubleValue()));
		attachListenerAndExecute(control.radiusProperty(), control::requestLayout);
		attachListenerAndExecute(control.thicknessProperty(), control::requestLayout);
		attachListenerAndExecute(control.getPaintAnimationSequence(), this::onPaintAnimationSequenceChanged);
		attachListenerAndExecute(control.getIconSequence(), this::checkForIconChange);
		attachListenerAndExecute(control.displayedIconProperty(), this::checkForIconChange);
	}

	protected double getArcSize(Supplier<Double> fallbackSupplier) {
		return control.getRadius() == Region.USE_COMPUTED_SIZE
				? fallbackSupplier.get()
				: (control.getRadius() + control.getThickness()) * 2;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getArcSize(() -> Double.MAX_VALUE);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getArcSize(() -> Double.MAX_VALUE);
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getArcSize(containerPane::getPrefWidth);
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getArcSize(containerPane::getPrefHeight);
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		double arcSize = getArcSize(() -> Math.min(contentWidth, contentHeight));
		containerPane.resize(Math.max(arcSize, contentWidth), Math.max(arcSize, contentHeight));

		double arcThickness = control.getThickness();
		double arcRadius = (arcSize / 2) - arcThickness;
		progressBar.setRadiusX(arcRadius);
		progressBar.setRadiusY(arcRadius);
		progressBar.setStrokeWidth(arcThickness);
		track.setRadiusX(arcRadius);
		track.setRadiusY(arcRadius);
		track.setStrokeWidth(arcThickness);

		progressRotationPane.setPrefWidth(arcSize);
		progressRotationPane.setPrefHeight(arcSize);

		Bounds layoutBounds = progressBar.getLayoutBounds();
		progressBar.relocate(
				((contentWidth - layoutBounds.getWidth()) / 2) + layoutBounds.getCenterX(),
				((contentHeight - layoutBounds.getHeight()) / 2) + layoutBounds.getCenterY()
		);

		if (currentShowAnimIcon != null) {
			double referenceRadius = currentShowAnimIcon.getReferenceRadius();
			referenceRadius = referenceRadius <= 0 ? arcRadius : referenceRadius; // if referenceRadius <= 0, then don't apply scaling
			double radiusScale = arcRadius / referenceRadius;

			iconSVGPath.setStrokeWidth(arcThickness / radiusScale);
			Bounds iconBounds = iconSVGPath.getLayoutBounds();
			iconSVGPath.relocate(
					((contentWidth - iconBounds.getWidth()) / 2) + (currentShowAnimIcon.getOffsetX() * radiusScale),
					((contentHeight - iconBounds.getHeight()) / 2) + (currentShowAnimIcon.getOffsetY() * radiusScale)
			);
			iconSVGPath.setScaleX(radiusScale);
			iconSVGPath.setScaleY(radiusScale);
		}

		if (progressText.isVisible()) {
			// assume font is generally twice as tall as it is wide
			// also assume 3 characters displayed on average -> 1.5 times wider than font-height
			// also assume 1/4 character width left/right of extra space for readability -> 1.75 times wider
			// also assume that 16px equal 12 point font size
			double innerCircleRadius = arcRadius - arcThickness;
			double targetFontHeight = (innerCircleRadius * 2 * 12 / 16) / 1.75;
			progressText.setFont(Font.font(progressText.getFont().getFamily(), Math.max(1, targetFontHeight)));
		}

		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
	}

	@Override
	public void dispose() {
		super.dispose();
		clearPaintTimeline();
		clearIndeterminateTimeline();
		clearProgressTextFadeAnimation();
	}

	private static record DeflateAnimationInfo(double deflateLength, double inflateLength, double rotationFactor) {
		public static DeflateAnimationInfo computeDeflateParameters(LoadingSpinnerSkin instance) {
			final double deflateLength = 5;
			final double inflateLength = instance.control.getProgress() != 0
					? Math.max(deflateLength, Math.abs(instance.control.getProgress()) * 360d)
					: (2d / 3d) * 360d;
			final double rotationFactor = instance.control.getProgress() > 0 ? -1 : 1; // rotate clockwise, if progress is > 0
			return new DeflateAnimationInfo(deflateLength, inflateLength, rotationFactor);
		}
	}
}
