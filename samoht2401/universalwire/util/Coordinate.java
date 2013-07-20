package samoht2401.universalwire.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraftforge.common.ForgeDirection;

public class Coordinate {

	public float x, y, z;

	public Coordinate(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean equals(Coordinate other) {
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		if (this.z != other.z)
			return false;
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Coordinate))
			return false;
		else {
			return this.equals((Coordinate) o);
		}
	}

	@Override
	public Coordinate clone() {
		return new Coordinate(this.x, this.y, this.z);
	}

	@Override
	public String toString() {
		return "Coordinate : " + this.x + " / " + this.y + " / " + this.z;
	}

	@Override
	public int hashCode() {
		return (int)this.x + (int)this.y >> 8 + (int)this.z >> 16;
	}

	public boolean isTouching(Coordinate other) {
		if (this.y == other.y && this.z == other.z && Math.abs(this.x - other.x) == 1) {
			return true;
		}
		if (this.x == other.x && this.z == other.z && Math.abs(this.y - other.y) == 1) {
			return true;
		}
		if (this.y == other.y && this.x == other.x && Math.abs(this.z - other.z) == 1) {
			return true;
		}
		return false;
	}

	public Coordinate getTouching(ForgeDirection dir) {
		Coordinate result = this.clone();
		result.x += dir.offsetX;
		result.y += dir.offsetY;
		result.z += dir.offsetZ;
		return result;
	}
}
