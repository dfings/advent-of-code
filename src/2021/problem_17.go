package main

import "fmt"

type Target struct {
	xMin, xMax, yMin, yMax int
}

func (t *Target) Miss(x int, y int) bool {
	return x > t.xMax || y < t.yMin
}

func (t *Target) Hit(x int, y int) bool {
	return x <= t.xMax && x >= t.xMin && y >= t.yMin && y <= t.yMax
}

func Max(x, y int) int {
	if x < y {
		return y
	}
	return x
}

func main() {
	t := Target{ xMin: 150, xMax: 171, yMin: -129, yMax: -70}
	yMax := 0
	hits := 0
	for x := 1; x <= t.xMax; x++ {
		for y := t.yMin; y <= -t.yMin; y++ {
			px, py, vx, vy := 0, 0, x, y
			for !t.Miss(px, py) {
				yMax = Max(yMax, py)
				if t.Hit(px, py) {
					hits++
					break
				}
				px += vx
				py += vy
				vx = Max(0, vx-1)
				vy -= 1
			}
		}
	}
	fmt.Printf("%d %d\n", yMax, hits)
}
