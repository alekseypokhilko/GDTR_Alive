package org.happysanta.gdtralive.game.util.mrg;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Deprecated
public class Reader {

	private static char[] cp1251Map = new char[]{
			'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
			'\u0008', '\u0009', '\n', '\u000B', '\u000C', '\r', '\u000E', '\u000F',
			'\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017',
			'\u0018', '\u0019', '\u001A', '\u001B', '\u001C', '\u001D', '\u001E', '\u001F',
			'\u0020', '\u0021', '\u0022', '\u0023', '\u0024', '\u0025', '\u0026', '\'',
			'\u0028', '\u0029', '\u002A', '\u002B', '\u002C', '\u002D', '\u002E', '\u002F',
			'\u0030', '\u0031', '\u0032', '\u0033', '\u0034', '\u0035', '\u0036', '\u0037',
			'\u0038', '\u0039', '\u003A', '\u003B', '\u003C', '\u003D', '\u003E', '\u003F',
			'\u0040', '\u0041', '\u0042', '\u0043', '\u0044', '\u0045', '\u0046', '\u0047',
			'\u0048', '\u0049', '\u004A', '\u004B', '\u004C', '\u004D', '\u004E', '\u004F',
			'\u0050', '\u0051', '\u0052', '\u0053', '\u0054', '\u0055', '\u0056', '\u0057',
			'\u0058', '\u0059', '\u005A', '\u005B', '\\', '\u005D', '\u005E', '\u005F',
			'\u0060', '\u0061', '\u0062', '\u0063', '\u0064', '\u0065', '\u0066', '\u0067',
			'\u0068', '\u0069', '\u006A', '\u006B', '\u006C', '\u006D', '\u006E', '\u006F',
			'\u0070', '\u0071', '\u0072', '\u0073', '\u0074', '\u0075', '\u0076', '\u0077',
			'\u0078', '\u0079', '\u007A', '\u007B', '\u007C', '\u007D', '\u007E', '\u007F',
			'\u0402', '\u0403', '\u201A', '\u0453', '\u201E', '\u2026', '\u2020', '\u2021',
			'\u20AC', '\u2030', '\u0409', '\u2039', '\u040A', '\u040C', '\u040B', '\u040F',
			'\u0452', '\u2018', '\u2019', '\u201C', '\u201D', '\u2022', '\u2013', '\u2014',
			'\uFFFD', '\u2122', '\u0459', '\u203A', '\u045A', '\u045C', '\u045B', '\u045F',
			'\u00A0', '\u040E', '\u045E', '\u0408', '\u00A4', '\u0490', '\u00A6', '\u00A7',
			'\u0401', '\u00A9', '\u0404', '\u00AB', '\u00AC', '\u00AD', '\u00AE', '\u0407',
			'\u00B0', '\u00B1', '\u0406', '\u0456', '\u0491', '\u00B5', '\u00B6', '\u00B7',
			'\u0451', '\u2116', '\u0454', '\u00BB', '\u0458', '\u0405', '\u0455', '\u0457',
			'\u0410', '\u0411', '\u0412', '\u0413', '\u0414', '\u0415', '\u0416', '\u0417',
			'\u0418', '\u0419', '\u041A', '\u041B', '\u041C', '\u041D', '\u041E', '\u041F',
			'\u0420', '\u0421', '\u0422', '\u0423', '\u0424', '\u0425', '\u0426', '\u0427',
			'\u0428', '\u0429', '\u042A', '\u042B', '\u042C', '\u042D', '\u042E', '\u042F',
			'\u0430', '\u0431', '\u0432', '\u0433', '\u0434', '\u0435', '\u0436', '\u0437',
			'\u0438', '\u0439', '\u043A', '\u043B', '\u043C', '\u043D', '\u043E', '\u043F',
			'\u0440', '\u0441', '\u0442', '\u0443', '\u0444', '\u0445', '\u0446', '\u0447',
			'\u0448', '\u0449', '\u044A', '\u044B', '\u044C', '\u044D', '\u044E', '\u044F'
	};

	private static final int MAX_VALID_TRACKS = 16384;

	public static LevelHeader readHeader(byte[] bytes) {
		LevelHeader header = new LevelHeader();
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			 DataInputStream din = new DataInputStream(in)) {
			byte[] buf = new byte[40];
			String tmp;
			for (int i = 0; i < 3; i++) {
				int tCount = din.readInt();
				if (tCount > MAX_VALID_TRACKS) {
					din.close();
					throw new IOException("Level file is not valid");
				}
				header.setCount(i, tCount);

				label0:
				for (int j = 0; j < header.getCount(i); j++) {
					int trackPointer = din.readInt();
					header.setPointer(i, j, trackPointer);
					int nameLen = 0;
					do {
						if (nameLen >= 40)
							continue label0;

						buf[nameLen] = din.readByte();
						if (buf[nameLen] == 0) {
							// tmp = (new String(buf, 0, nameLen, "CP-1251"));
							tmp = decodeCp1251(buf);
							header.setName(i, j, tmp.replace('_', ' '));
							continue label0;
						}
						nameLen++;
					} while (true);
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return header;
	}

	public static String decodeCp1251(byte[] data) {
		if (data == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder(data.length);
		for (byte datum : data) {
			if (datum == 0) break;
			sb.append(cp1251Map[datum & 0xFF]);
		}
		return sb.toString();
	}
}
