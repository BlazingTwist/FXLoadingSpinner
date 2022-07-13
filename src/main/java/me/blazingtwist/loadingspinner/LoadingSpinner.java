package me.blazingtwist.loadingspinner;

import java.util.List;
import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

/**
 * Fields:
 * <p>{@link LoadingSpinner#progress}</p>
 * <p>{@link LoadingSpinner#progressText}</p>
 * <p>{@link LoadingSpinner#indeterminate}</p>
 * <p>{@link LoadingSpinner#startAngle}</p>
 * <p>{@link LoadingSpinner#radius}</p>
 * <p>{@link LoadingSpinner#thickness}</p>
 * <p>{@link LoadingSpinner#paintAnimationSequence}</p>
 * <p>{@link LoadingSpinner#iconSequence}</p>
 */
public class LoadingSpinner extends Control {
	public static final String css_styleClass = "loading-spinner";
	public static final String css_property_progress = "-ls-progress";
	public static final String css_property_progress_text = "-ls-progress-text";
	public static final String css_property_indeterminate = "-ls-indeterminate";
	public static final String css_property_startAngle = "-ls-start-angle";
	public static final String css_property_radius = "-ls-radius";
	public static final String css_property_thickness = "-ls-thickness";

	/**
	 * <p>Specifies the fill rate and direction of the bar. [-1, +1]</p>
	 * <p>Negative numbers fill the bar in a counter-clockwise direction from the startAngle.</p>
	 * <p>Positive numbers fill in a clockwise direction.</p>
	 *
	 * <p>default is 0</p>
	 * <p>can be specified with css {@link LoadingSpinner#css_property_progress}</p>
	 */
	private final StyleableDoubleProperty progress = new SimpleStyleableDoubleProperty(
			StyleableProperties.css_progress, LoadingSpinner.this, "progress", 0d
	);

	/**
	 * <p>If enabled, Spinner will additionally display the progress as text. (e.g. '42%')</p>
	 *
	 * <p>default is false</p>
	 * <p>can be specified with css {@link LoadingSpinner#css_property_progress_text}</p>
	 */
	private final StyleableBooleanProperty progressText = new SimpleStyleableBooleanProperty(
			StyleableProperties.css_progress_text, LoadingSpinner.this, "progressText", Boolean.FALSE
	);

	/**
	 * <p>If enabled, Spinner will play a rotating, inflating/deflating animation.</p>
	 *
	 * <p>You can control the direction of rotation and maximum fill rate by specifying non-zero progress,
	 * otherwise (counterclockwise, [5°, 250°]) will be used.</p>
	 *
	 * <p>For example, 'progress = 0.4' results in an animation with clockwise rotation, deflating to 5° and inflating to 144° (0.4 * 360)</p>
	 *
	 * <p>default is false</p>
	 * <p>can be specified with css {@link LoadingSpinner#css_property_indeterminate}</p>
	 */
	private final SimpleStyleableBooleanProperty indeterminate = new SimpleStyleableBooleanProperty(
			StyleableProperties.css_indeterminate, LoadingSpinner.this, "indeterminate", false
	);

	/**
	 * <p>Specifies the angle (in degrees) from which the bar starts filling. [-360, +360]</p>
	 *
	 * <p>When 'indeterminate' is enabled, this specifies an offset to the animation.
	 * The animation will be offset by the specified startAngle, in the direction of rotation.</p>
	 *
	 * <p>The angle is measured clockwise starting from the 3 o'clock position.</p>
	 *
	 * <p>default is 0</p>
	 * <p>can be specified with css {@link LoadingSpinner#css_property_startAngle}</p>
	 */
	private final StyleableDoubleProperty startAngle = new SimpleStyleableDoubleProperty(
			StyleableProperties.css_startAngle, LoadingSpinner.this, "startAngle", 0d
	);

	/**
	 * <p>Radius of the progress bar.</p>
	 *
	 * <p>default is -1 (try fill parent | Region.USE_COMPUTED_SIZE)</p>
	 * <p>can be specified with css {@link LoadingSpinner#css_property_radius}</p>
	 */
	private final StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(
			StyleableProperties.css_radius, LoadingSpinner.this, "radius", Region.USE_COMPUTED_SIZE
	);

	/**
	 * <p>Thickness of the progress bar.</p>
	 * <p>Also sets the stroke-width for animated SVG paths.</p>
	 *
	 * <p>default is 1</p>
	 * <p>can be specified with css {@link LoadingSpinner#css_property_thickness}</p>
	 */
	private final StyleableDoubleProperty thickness = new SimpleStyleableDoubleProperty(
			StyleableProperties.css_thickness, LoadingSpinner.this, "thickness", 1d
	);

	/**
	 * <p>May contain 0 or more info objects that define the color of the progress bar.</p>
	 */
	private final ObservableList<LoadingSpinnerPaintAnimationInfo> paintAnimationSequence = new SimpleListProperty<>(
			LoadingSpinner.this, "paintAnimationSequence", FXCollections.observableArrayList()
	);

	/**
	 * <p>May contain 0 or more animated icons.</p>
	 */
	private final ObservableList<LoadingSpinnerAnimatedIcon> iconSequence = new SimpleListProperty<>(
			LoadingSpinner.this, "iconSequence", FXCollections.observableArrayList()
	);

	/**
	 * <p>A key to an Icon in the {@link LoadingSpinner#iconSequence}, or null if no Icon should be shown.</p>
	 * <p>The Spinner will automatically handle animating to this icon when this property changes.</p>
	 */
	private final ObjectProperty<IconKey> displayedIcon = new SimpleObjectProperty<>(
			LoadingSpinner.this, "displayedIcon", null
	);

	public LoadingSpinner() {
		getStyleClass().add(css_styleClass);
	}

	/**
	 * {@link LoadingSpinner#progress see field javadoc}
	 */
	public StyleableDoubleProperty progressProperty() {
		return progress;
	}

	/**
	 * {@link LoadingSpinner#progressText see field javadoc}
	 */
	public StyleableBooleanProperty progressTextProperty() {
		return progressText;
	}

	/**
	 * {@link LoadingSpinner#indeterminate see field javadoc}
	 */
	public SimpleStyleableBooleanProperty indeterminateProperty() {
		return indeterminate;
	}

	/**
	 * {@link LoadingSpinner#startAngle see field javadoc}
	 */
	public StyleableDoubleProperty startAngleProperty() {
		return startAngle;
	}

	/**
	 * {@link LoadingSpinner#radius see field javadoc}
	 */
	public StyleableDoubleProperty radiusProperty() {
		return radius;
	}

	/**
	 * {@link LoadingSpinner#thickness see field javadoc}
	 */
	public StyleableDoubleProperty thicknessProperty() {
		return thickness;
	}

	/**
	 * {@link LoadingSpinner#paintAnimationSequence see field javadoc}
	 */
	public ObservableList<LoadingSpinnerPaintAnimationInfo> getPaintAnimationSequence() {
		return paintAnimationSequence;
	}

	/**
	 * {@link LoadingSpinner#iconSequence see field javadoc}
	 */
	public ObservableList<LoadingSpinnerAnimatedIcon> getIconSequence() {
		return iconSequence;
	}

	/**
	 * {@link LoadingSpinner#displayedIcon see field javadoc}
	 */
	public ObjectProperty<IconKey> displayedIconProperty() {
		return displayedIcon;
	}

	/**
	 * sets {@link LoadingSpinner#displayedIcon} to target the icon at the given index in the {@link LoadingSpinner#iconSequence}
	 */
	public void displayIconByIndex(int index) {
		displayedIcon.set(IconKey.getByIndex(index));
	}

	/**
	 * sets {@link LoadingSpinner#displayedIcon} to target the icon at the given index in the {@link LoadingSpinner#iconSequence}
	 */
	public void displayIconByKey(String key) {
		displayedIcon.set(IconKey.getByKey(key));
	}

	/**
	 * @param key the {@link IconKey} to query the {@link LoadingSpinner#iconSequence} for
	 * @return the first matching {@link LoadingSpinnerAnimatedIcon} or null
	 */
	public LoadingSpinnerAnimatedIcon getAnimatedIcon(IconKey key) {
		if (key == null) {
			return null;
		}

		if (key.index != null) {
			int index = key.index;
			if (index >= 0 && index < iconSequence.size()) {
				return iconSequence.get(index);
			}
		}

		if (key.key != null) {
			return iconSequence.stream()
					.filter(icon -> key.key.equals(icon.getKey()))
					.findFirst().orElse(null);
		}
		return null;
	}

	public double getProgress() {
		return progress.get();
	}

	public void setProgress(double progress) {
		this.progress.set(progress);
	}

	public boolean isProgressText() {
		return progressText.get();
	}

	public void setProgressText(boolean progressText) {
		this.progressText.set(progressText);
	}

	public boolean isIndeterminate() {
		return indeterminate.get();
	}

	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate.set(indeterminate);
	}

	public double getStartAngle() {
		return startAngle.get();
	}

	public void setStartAngle(double startAngle) {
		this.startAngle.set(startAngle);
	}

	public double getRadius() {
		return radius.get();
	}

	public void setRadius(double radius) {
		this.radius.set(radius);
	}

	public double getThickness() {
		return thickness.get();
	}

	public void setThickness(double thickness) {
		this.thickness.set(thickness);
	}

	public IconKey getDisplayedIcon() {
		return displayedIcon.get();
	}

	public void setDisplayedIcon(IconKey displayedIcon) {
		this.displayedIcon.set(displayedIcon);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new LoadingSpinnerSkin(this);
	}

	@Override
	protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}

	private static class StyleableProperties {

		private static <T, TProp extends Property<T> & StyleableProperty<T>> CssMetaData<LoadingSpinner, T> getCssMetaData(
				String cssPropertyKey, StyleConverter<?, T> converter, T defaultValue, Function<LoadingSpinner, TProp> propertyGetter) {

			return new CssMetaData<>(cssPropertyKey, converter, defaultValue) {
				@Override
				public boolean isSettable(LoadingSpinner loadingSpinner) {
					TProp property = propertyGetter.apply(loadingSpinner);
					return property == null || !property.isBound();
				}

				@Override
				public StyleableProperty<T> getStyleableProperty(LoadingSpinner loadingSpinner) {
					return propertyGetter.apply(loadingSpinner);
				}
			};
		}

		private static final CssMetaData<LoadingSpinner, Number> css_progress;
		private static final CssMetaData<LoadingSpinner, Boolean> css_progress_text;
		private static final CssMetaData<LoadingSpinner, Boolean> css_indeterminate;
		private static final CssMetaData<LoadingSpinner, Number> css_startAngle;
		private static final CssMetaData<LoadingSpinner, Number> css_radius;
		private static final CssMetaData<LoadingSpinner, Number> css_thickness;
		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

		static {
			css_progress = getCssMetaData(css_property_progress, SizeConverter.getInstance(), 0d, LoadingSpinner::progressProperty);
			css_progress_text = getCssMetaData(css_property_progress_text, BooleanConverter.getInstance(), Boolean.FALSE, LoadingSpinner::progressTextProperty);
			css_indeterminate = getCssMetaData(css_property_indeterminate, BooleanConverter.getInstance(), Boolean.FALSE, LoadingSpinner::indeterminateProperty);
			css_startAngle = getCssMetaData(css_property_startAngle, SizeConverter.getInstance(), 0d, LoadingSpinner::startAngleProperty);
			css_radius = getCssMetaData(css_property_radius, SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE, LoadingSpinner::radiusProperty);
			css_thickness = getCssMetaData(css_property_thickness, SizeConverter.getInstance(), 1d, LoadingSpinner::thicknessProperty);

			STYLEABLES = List.of(
					css_progress,
					css_indeterminate,
					css_startAngle,
					css_radius,
					css_thickness
			);
		}
	}
}
