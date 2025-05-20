# Mandelbrot Media Generator

<div align="center">
	<p>
		<img src="examples/example1.gif" height="250">
		<img src="examples/example2.gif" height="250">
	</p>
</div>

The Mandelbrot set is infinitely complex. As the zoom increases, a completely unique fractal pattern is generated, meaning that generated images are likely to have never been seen before.

## How To Run

1. Install Maven
2. Clone the repo and navigate to it's top level
3. Run the command: `make mandelbrot`

## Usage

- Move `Up`/`Down`/`Left`/`Right` with `W`/`S`/`A`/`D`
- `Zoom in` with `UP ARROW`
- `Zoom out` with `DOWN ARROW`
- `Reset` with `R`
- Toggle `show zoom` with `Z`
- Toggle `cursor control` with `C`
- Take `screenshot` with `I`
- `Generate gif` of zoom to current point with `ENTER`

Screenshots can be found in `output/images`  
Gifs can be found in `output/media`  
Frames of the previously generated gif can be found in `output/frames`  