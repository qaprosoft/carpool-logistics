package com.asemenkov.carpool.logistics;

import com.asemenkov.carpool.logistics.services.googlemaps.GoogleMapsPoint;

/**
 * @author asemenkov
 * @since Feb 12, 2018
 */
public class RealWorldData {

	public static final GoogleMapsPoint[] REAL_POINTS = { //
			new GoogleMapsPoint(50.403299, 30.516734), new GoogleMapsPoint(50.503801, 30.440792), //
			new GoogleMapsPoint(50.508901, 30.436756), new GoogleMapsPoint(50.351568, 30.448268), //
			new GoogleMapsPoint(50.481070, 30.397362), new GoogleMapsPoint(50.469404, 30.330892), //
			new GoogleMapsPoint(50.416559, 30.483263), new GoogleMapsPoint(50.413433, 30.396862), //
			new GoogleMapsPoint(50.451673, 30.489563), new GoogleMapsPoint(50.436982, 30.602029), //
			new GoogleMapsPoint(50.426899, 30.392561), new GoogleMapsPoint(50.472111, 30.499026), //
			new GoogleMapsPoint(50.376879, 30.538773), new GoogleMapsPoint(50.393299, 30.646354), //
			new GoogleMapsPoint(50.520030, 30.617247), new GoogleMapsPoint(50.527226, 30.620744) };

	public static final int[][] REAL_DISTANCES = { //
			{ 0, 18931, 19720, 10019, 16279, 20098, 5395, 13449, 7064, 7953, 15263, 13922, 4206, 10312, 20995, 20907 },
			{ 19635, 0, 2641, 27626, 6338, 11912, 14549, 21275, 8024, 18198, 15577, 7386, 21729, 24349, 16913, 16825 },
			{ 20014, 1700, 0, 28801, 7512, 13087, 14928, 22450, 8944, 18576, 15955, 7764, 22107, 24727, 17292, 17204 },
			{ 9012, 27575, 33985, 0, 22707, 20284, 10145, 10960, 16639, 22378, 12773, 28188, 14127, 23087, 35261,
					35173 },
			{ 16612, 6522, 7616, 22048, 0, 8518, 12131, 15697, 10011, 24380, 8625, 11444, 18941, 25047, 20804, 20716 },
			{ 22747, 12276, 13369, 20287, 8612, 0, 17047, 13936, 14927, 25251, 10678, 19818, 24850, 29963, 29179,
					29091 },
			{ 5987, 13927, 16061, 11446, 12505, 16324, 0, 11585, 5263, 12124, 10672, 8971, 8030, 14135, 23605, 23517 },
			{ 15213, 18389, 19482, 12753, 13521, 11098, 10703, 0, 10665, 21607, 2529, 14372, 17513, 23618, 25830,
					25742 },
			{ 7551, 8184, 9109, 17456, 10293, 14112, 5672, 13252, 0, 11274, 9927, 4214, 10116, 15515, 21579, 21491 },
			{ 8371, 17069, 17858, 17136, 19742, 23561, 10657, 20566, 10527, 0, 22380, 12060, 10554, 9133, 13824,
					13736 },
			{ 16613, 18560, 16571, 14153, 13016, 11269, 8988, 3990, 8950, 19893, 0, 12658, 15798, 21904, 24115, 24027 },
			{ 15001, 5707, 6631, 23766, 9402, 17454, 13089, 14950, 5914, 13563, 14117, 0, 17094, 19714, 15669, 15581 },
			{ 4411, 21407, 22195, 14569, 19201, 23020, 7383, 19732, 10386, 10588, 21546, 16398, 0, 11297, 23471,
					23383 },
			{ 12152, 24047, 24835, 24295, 25195, 29014, 13981, 29458, 15979, 8969, 23965, 19038, 11920, 0, 26111,
					26023 },
			{ 20419, 15287, 16075, 29184, 18769, 26821, 22705, 24317, 17475, 13155, 23484, 15363, 25126, 17414, 0,
					2412 },
			{ 21168, 16811, 17600, 29932, 20294, 28345, 23454, 25841, 19000, 13904, 25009, 16887, 26650, 18163, 2655,
					0 } };

	public static final int[][] REAL_DURATIONS = { //
			{ 0, 1645, 1732, 1045, 1689, 1981, 573, 1333, 1047, 710, 1551, 1178, 458, 798, 1734, 1715 },
			{ 1735, 0, 447, 2436, 738, 1202, 1651, 1993, 1334, 1745, 1702, 891, 1826, 2009, 1593, 1575 },
			{ 1895, 461, 0, 2679, 981, 1445, 1811, 2236, 1474, 1906, 1862, 1052, 1986, 2169, 1753, 1735 },
			{ 909, 2440, 2518, 0, 1935, 1824, 1193, 1109, 1925, 1560, 1327, 1964, 1159, 1576, 2520, 2501 },
			{ 1857, 777, 995, 1873, 0, 1013, 1389, 1431, 1135, 2101, 1189, 1251, 2011, 2351, 1965, 1946 },
			{ 2052, 1348, 1564, 1882, 1031, 0, 1745, 1440, 1491, 2667, 1306, 1908, 2377, 2708, 2623, 2604 },
			{ 785, 1669, 1775, 1403, 1203, 1495, 0, 1335, 903, 1284, 1166, 1471, 974, 1314, 2161, 2143 },
			{ 1507, 1804, 2020, 1337, 1300, 1188, 1188, 0, 1112, 2120, 489, 1680, 1810, 2151, 2374, 2355 },
			{ 1089, 1351, 1503, 1934, 935, 1228, 913, 1400, 0, 1571, 1085, 851, 1390, 1638, 1931, 1913 },
			{ 831, 1718, 1806, 1695, 2140, 2432, 1111, 1983, 1528, 0, 2201, 1251, 994, 1149, 1620, 1601 },
			{ 1531, 1795, 1819, 1362, 1247, 1180, 1020, 624, 943, 1952, 0, 1512, 1642, 1982, 2205, 2187 },
			{ 1331, 842, 1020, 2195, 1152, 1717, 1478, 1865, 1068, 1342, 1529, 0, 1423, 1606, 1517, 1498 },
			{ 567, 1788, 1875, 1198, 1876, 2169, 799, 1580, 1476, 917, 1798, 1321, 0, 933, 1877, 1858 },
			{ 1159, 2010, 2097, 1759, 2321, 2614, 1190, 2140, 1707, 1184, 2230, 1543, 1019, 0, 2098, 2080 },
			{ 1966, 1622, 1710, 2830, 1882, 2447, 2245, 2595, 1988, 1688, 2259, 1476, 1963, 2146, 0, 498 },
			{ 1946, 1624, 1711, 2810, 1884, 2448, 2225, 2597, 1990, 1668, 2260, 1447, 1964, 2126, 405, 0 } };

}