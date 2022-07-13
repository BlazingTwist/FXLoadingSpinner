package me.blazingtwist.loadingspinner;

import javafx.beans.NamedArg;
import javafx.scene.paint.Paint;

/**
 * Constants:
 * <p>{@link #greenCheckMark}</p>
 * <p>{@link #yellowExclamationMark}</p>
 * <p>{@link #redCross}</p>
 * <p></p>
 * Fields:
 * <p>{@link LoadingSpinnerAnimatedIcon#key}</p>
 * <p>{@link LoadingSpinnerAnimatedIcon#path} (required)</p>
 * <p>{@link LoadingSpinnerAnimatedIcon#pathLength}</p>
 * <p>{@link LoadingSpinnerAnimatedIcon#paint}</p>
 * <p>{@link LoadingSpinnerAnimatedIcon#gapWidth}</p>
 * <p>{@link LoadingSpinnerAnimatedIcon#gapAngle}</p>
 * <p>{@link LoadingSpinnerAnimatedIcon#offsetX}</p>
 * <p>{@link LoadingSpinnerAnimatedIcon#offsetY}</p>
 */
public class LoadingSpinnerAnimatedIcon {

	/**
	 * A green check mark, useful to indicate a successful status.
	 * <p>key: 'greenCheckMark'</p>
	 */
	public static final LoadingSpinnerAnimatedIcon greenCheckMark = new LoadingSpinnerAnimatedIcon("greenCheckMark",
			"M 0 0 q 5 6 8 10 q 12 -18 20 -24", 46d, 23d,
			Paint.valueOf("#47da37"), 60d, -38d, 8d, -5d
	);

	/**
	 * A yellow exclamation mark, useful to indicate warnings.
	 * <p>key: 'yellowExclamationMark'</p>
	 */
	public static final LoadingSpinnerAnimatedIcon yellowExclamationMark = new LoadingSpinnerAnimatedIcon("yellowExclamationMark",
			"M 0 0 l -2 -30 m 2 30 l 2 -30 m -2 40 l 0 -6", 40d, 23d,
			Paint.valueOf("#f3d513"), 70d, -90d, 0d, -5d
	);

	/**
	 * A red cross, useful to indicate errors.
	 * <p>key: 'redCross'</p>
	 */
	public static final LoadingSpinnerAnimatedIcon redCross = new LoadingSpinnerAnimatedIcon("redCross",
			"M 0 0 m 0 18 l 18 -18 m 0 18 l -18 -18", 28d, 23d,
			Paint.valueOf("#da3737"), 0d, -45d, 0d, 0d
	);

	/**
	 * <p>Lets you specify a string key to access this icon. Otherwise you need to access it by index (int).</p>
	 * <p>Default is 'null' (access only via index)</p>
	 */
	private final String key;

	/**
	 * <p>WARNING: There is no default value for this. Specify this or I am going to slap you with a NullPointerException.</p>
	 * <p>The <a href="https://www.w3schools.com/graphics/svg_path.asp">svg path</a> to stroke.</p>
	 * <p>For creating these paths, you should use the {@link LoadingSpinnerAnimatedIcon#referenceRadius}, your path will be scaled accordingly.</p>
	 */
	private final String path;

	/**
	 * <p>The length of your svg path.</p>
	 * <p>You can determine the length of your path by guessing or calculating it:</p>
	 * <ul>
	 *     <li>
	 *         <b>guessing</b>: increase value until path becomes invisible, decrease until you can barely see it, increase by 1 or 2 (path should be invisible)
	 *     </li>
	 *     <li>
	 *         <p><b>calculating</b>: As an estimate, you can take the sum of the euclidean distance of each step along the path.
	 *         <p>For example: 'M 0 0 l 10 8 l 6 4' has two steps: (10, 8) and (6, 4). With euclidean distances sqrt(10 * 10 + 8 * 8) and sqrt(6 * 6 + 4 * 4)
	 *         <p>Leaving you with a total approximate length of '20'. For good measure, you can/should increase this value by 1 or 2.
	 *     </li>
	 * </ul>
	 * <p>Default is '0' - this will disable the animation, causing the path to always be visible.</p>
	 */
	private final double pathLength;

	/**
	 * <p>The spinner radius your path was designed for.</p>
	 * <p>For example, if your reference radius is 20, but the spinner radius is 40, your path will be scaled by a factor of 2</p>
	 * <p>You can also specify a radius &lt;= 0. Then your path won't be scaled to match the spinner.</p>
	 * <p>Default is '10'</p>
	 */
	private final double referenceRadius;

	/**
	 * <p>Specifies the paint for both the loading bar and the stroked path.</p>
	 * <p>May be null, in which case the progress bar color behaviour is not changed. (e.g. remains fixed / animated)</p>
	 * <p>Default is null</p>
	 */
	private final Paint paint;

	/**
	 * <p>Allows you to specify an opening in the surrounding progress bar (e.g. to create overlap with your path)</p>
	 * <p>Specifies the width of the gap in °degrees. [0, 360]</p>
	 * <p>Should be used in combination with {@link LoadingSpinnerAnimatedIcon#gapAngle}</p>
	 * <p>Default is '0'</p>
	 */
	private final double gapWidth;

	/**
	 * <p>Angle in °degrees where the center of the gap should be. [-360, +360]</p>
	 * <p>The angle is measured clockwise starting from the 3 o'clock position.</p>
	 * <p>Default is '-45'</p>
	 */
	private final double gapAngle;

	/**
	 * <p>Offset for this icon in x direction. Icons will be centered by default.</p>
	 * <p>Default is '0'</p>
	 */
	private final double offsetX;

	/**
	 * <p>Offset for this icon in y direction. Icons will be centered by default.</p>
	 * <p>Default is '0'</p>
	 */
	private final double offsetY;

	/**
	 * @param key             {@link LoadingSpinnerAnimatedIcon#key see field javadoc}
	 * @param path            {@link LoadingSpinnerAnimatedIcon#path see field javadoc}
	 * @param pathLength      {@link LoadingSpinnerAnimatedIcon#pathLength see field javadoc}
	 * @param referenceRadius {@link LoadingSpinnerAnimatedIcon#referenceRadius see field javadoc}
	 * @param paint           {@link LoadingSpinnerAnimatedIcon#paint see field javadoc}
	 * @param gapWidth        {@link LoadingSpinnerAnimatedIcon#gapWidth see field javadoc}
	 * @param gapAngle        {@link LoadingSpinnerAnimatedIcon#gapAngle see field javadoc}
	 * @param offsetX         {@link LoadingSpinnerAnimatedIcon#offsetX see field javadoc}
	 * @param offsetY         {@link LoadingSpinnerAnimatedIcon#offsetY see field javadoc}
	 */
	public LoadingSpinnerAnimatedIcon(
			@NamedArg("key") String key,
			@NamedArg("path") String path,
			@NamedArg("pathLength") Double pathLength,
			@NamedArg("referenceRadius") Double referenceRadius,
			@NamedArg("paint") Paint paint,
			@NamedArg("gapWidth") Double gapWidth,
			@NamedArg("gapAngle") Double gapAngle,
			@NamedArg("offsetX") Double offsetX,
			@NamedArg("offsetY") Double offsetY) {
		this.key = key;
		this.path = path;
		this.pathLength = pathLength != null ? pathLength : 0;
		this.referenceRadius = referenceRadius != null ? referenceRadius : 10;
		this.paint = paint;
		this.gapWidth = gapWidth != null ? gapWidth : 0;
		this.gapAngle = gapAngle != null ? gapAngle : -45;
		this.offsetX = offsetX != null ? offsetX : 0;
		this.offsetY = offsetY != null ? offsetY : 0;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#key see field javadoc}
	 */
	public String getKey() {
		return key;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#path see field javadoc}
	 */
	public String getPath() {
		return path;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#pathLength see field javadoc}
	 */
	public double getPathLength() {
		return pathLength;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#referenceRadius see field javadoc}
	 */
	public double getReferenceRadius() {
		return referenceRadius;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#paint see field javadoc}
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#gapWidth see field javadoc}
	 */
	public double getGapWidth() {
		return gapWidth;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#gapAngle see field javadoc}
	 */
	public double getGapAngle() {
		return gapAngle;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#offsetX see field javadoc}
	 */
	public double getOffsetX() {
		return offsetX;
	}

	/**
	 * {@link LoadingSpinnerAnimatedIcon#offsetY see field javadoc}
	 */
	public double getOffsetY() {
		return offsetY;
	}

	@Override
	public String toString() {
		return "LoadingSpinnerAnimatedIcon{"
				+ "key: " + key
				+ ", path: " + path
				+ ", pathLength: " + pathLength
				+ ", referenceRadius: " + referenceRadius
				+ ", paint: " + paint
				+ ", gapWidth: " + gapWidth
				+ ", gapAngle: " + gapAngle
				+ ", offsetX: " + offsetX
				+ ", offsetY: " + offsetY
				+ '}';
	}
}
