package main

import "fmt"

func main() {
	xMin, xMax, yMin, yMax := 150, 171, -129, -70
	yBest := 0
	hits := 0
	for x := 1; x <= xMax; x++ {
		for y := yMin; y <= -yMin; y++ {
			px, py, vx, vy := 0, 0, x, y
			for px <= xMax && py >= yMin {
				if py > yBest {
					yBest = py
				}
				if px >= xMin && py <= yMax {
					hits++
					break
				}
				px, py, vy = px+vx, py+vy, vy-1
				if (vx != 0) {
					vx--
				}
			}
		}
	}
	fmt.Printf("%d %d\n", yBest, hits)
}
