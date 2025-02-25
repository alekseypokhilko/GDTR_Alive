package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.util.Utils;

import java.io.DataInputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Track related properties
 */
public class TrackParams implements Serializable {

	public int league;
	public int startX;
	public int startY;
	public int finishX;
	public int startPointIndex;
	public int finishPointIndex;
	public int finishY;
	public int pointsCount;
	public int[][] points;
	public transient int cameraX;
	public transient int cameraY;
	public transient int shadowX;
	public transient int shadowY;
	public transient int shadow_m_gI;
	public transient int shadow_m_rI;

	//NEW
	public String guid;
	public String name;
	public String author;
	public Set<Integer> invisible = new HashSet<>();
//	public Map<Integer, Integer> leagueSwitchers = new HashMap<>(); // point -> league
	public boolean checkFinishCoordinates = false;
	public boolean checkBackwardCollision = true;
	public Integer deadlineY;

	public TrackParams() {
		cameraX = 0;
		cameraY = 0;
		shadowX = 0;
		shadowY = 0;
		shadow_m_gI = 0;
		startPointIndex = 0;
		finishPointIndex = 0;
		points = (int[][]) null;
		name = "Unnamed";
		shadow_m_rI = 0;
		clear();
	}

	public void clear() {
		startX = 0;
		startY = 0;
		finishX = 0xc80000;
		pointsCount = 0;
	}

	public int _doII(int j) {
		int k = j - points[startPointIndex][0];
		int i1;
		if (((i1 = points[finishPointIndex][0] - points[startPointIndex][0]) >= 0 ? i1 : -i1) < 3 || k > i1)
			return 0x10000;
		else
			return (int) (((long) k << 32) / (long) i1 >> 16);
	}

	public void moveTrack(int j, int k) {
        cameraX = Utils.unpackInt(j);
        cameraY = Utils.unpackInt(k);
	}

	public void _aIIV(int j, int k) {
		shadowX = j >> 1;
		shadowY = k >> 1;
	}

	public void _aIIV(int j, int k, int i1) {
		shadowX = j;
		shadowY = k;
		shadow_m_gI = i1;
	}

	public void addPoint(int x, int y) {
		if (points == null || points.length <= pointsCount) {
			int i1 = 100;
			if (points != null)
				i1 = Math.max(i1, points.length + 30);
			int[][] ai = new int[i1][2];
			if (points != null)
				System.arraycopy(points, 0, ai, 0, points.length);
			points = ai;
		}
		if (pointsCount == 0 || points[pointsCount - 1][0] < x) {
			points[pointsCount][0] = x;
			points[pointsCount][1] = y;
			pointsCount++;
		}
	}

	public synchronized void readTrackData(DataInputStream in, boolean unpacked) {
		try {
			clear();
			if (in.readByte() == 50) {
				byte[] bytes = new byte[20];
				in.readFully(bytes);
			}
			finishPointIndex = 0;
			startPointIndex = 0;
			startX = in.readInt();
			startY = in.readInt();
			finishX = in.readInt();
			finishY = in.readInt();
			short pointsCount = in.readShort();
			int firstPointX = in.readInt();
			int firstPointY = in.readInt();
			int pointX = firstPointX; // -380
			int pointY = firstPointY; //  136
			//[-3112960, 1114112]
			//int[] unpacked = new int[] {(pointX << 16) >> 3, (pointY << 16) >> 3};
			//[-49, 24, 433]
//			int[] packed = new int[] {(startX << 3) >> 16, (startY << 3) >> 16, (finishX << 3) >> 16};

			//
			if (unpacked) {
                addPoint(Utils.unpackInt(pointX), Utils.unpackInt(pointY));
			} else {
				addPoint(pointX, pointY);
			}

			for (int i = 1; i < pointsCount; i++) {
				int x;
				int y;
				byte byte0;
				if ((byte0 = in.readByte()) == -1) {
					pointX = pointY = 0;
					x = in.readInt();
					y = in.readInt();
				} else {
					x = byte0;
					y = in.readByte();
				}
				pointX += x;
				pointY += y;
				if (unpacked) {
                    addPoint(Utils.unpackInt(pointX), Utils.unpackInt(pointY));
				} else {
					addPoint(pointX, pointY);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getLeague() {
		return league;
	}

	public void setLeague(int league) {
		this.league = league;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
