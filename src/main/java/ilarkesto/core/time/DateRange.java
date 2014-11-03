/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.time;

import ilarkesto.core.base.Args;
import ilarkesto.core.base.Str.Formatable;

import java.io.Serializable;

public class DateRange implements Comparable<DateRange>, Serializable, Formatable {

	protected final Date start;
	protected final Date end;

	private transient int hashCode;

	public DateRange(String s) {
		Args.assertNotNull(s, "s");
		int separatorIdx = s.indexOf(" - ");
		if (separatorIdx < 0) throw new IllegalArgumentException("Illegal DateRange: " + s);
		start = new Date(s.substring(0, separatorIdx));
		end = new Date(s.substring(separatorIdx + 3));
		check();
	}

	public DateRange(Date start, Date end) {
		Args.assertNotNull(start, "start", end, "end");
		this.start = start;
		this.end = end;
		check();
	}

	public DateRange(Date singleDay) {
		this(singleDay, singleDay);
	}

	public DateRange(java.util.Date start, java.util.Date end) {
		this(new Date(start), new Date(end));
		check();
	}

	private void check() {
		if (start.isAfter(end))
			throw new IllegalArgumentException("Illegal date range. Start is after end: " + toString());
	}

	public boolean isWholeMonth() {
		if (!isSameMonthAndYear()) return false;
		return start.isFirstDayOfMonth() && end.isLastDateOfMonth();
	}

	public boolean isSameYear() {
		return start.year == end.year;
	}

	public boolean isSameMonthAndYear() {
		return start.month == end.month && isSameYear();
	}

	public boolean isOneDay() {
		return start.equals(end);
	}

	public int getDayCount() {
		return Tm.getDaysBetweenDates(start.toJavaDate(), end.toJavaDate()) + 1;
	}

	public TimePeriod getTimePeriodBetweenStartAndEnd() {
		return start.getPeriodTo(end);
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	@Override
	public String format() {
		if (isOneDay()) return start.format();
		return start.format() + " - " + end.format();
	}

	public String formatStartLongMonthYear() {
		return start.formatLongMonthYear();
	}

	public String formatShortest() {
		if (isOneDay()) return start.format();

		if (isSameYear() && start.isFirstDayOfYear() && end.isLastDayOfYear()) return String.valueOf(start.getYear());

		if (start.equals(start.getFirstDateOfYear()) && end.equals(end.getLastDateOfYear())) {
			// erster eines Jahres bis letzter eines (evtl. anderen) Jahres
			if (start.getYear() == end.getYear()) return start.getYear() + "";
			return start.getYear() + " - " + end.getYear();
		}

		if (start.equals(start.getFirstDateOfMonth()) && end.equals(end.getLastDateOfMonth())) {
			// erster eines Monats bis letzter eines (evtl. anderen) Monats
			if (start.getMonth() == end.getMonth()) return start.formatLongMonthYear();
			return start.formatLongMonthYear() + " - " + end.formatLongMonthYear();
		}

		return start.formatDayMonthYear() + " - " + end.formatDayMonthYear();
	}

	@Override
	public int compareTo(DateRange o) {
		return start.compareTo(o.start);
	}

	@Override
	public String toString() {
		return start.toString() + " - " + end.toString();
	}

	@Override
	public final int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + start.hashCode();
			hashCode = hashCode * 37 + end.hashCode();
		}
		return hashCode;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof DateRange)) return false;
		return start.equals(((DateRange) obj).start) && end.equals(((DateRange) obj).end);
	}

	public boolean contains(Date date) {
		if (date == null) return false;
		return start.isBeforeOrSame(date) && end.isAfterOrSame(date);
	}

	/**
	 * @return cout of the overlapping days of this DateRange with the given DateRange
	 */
	public int getOverlappingDays(DateRange other) {
		if (other == null) return 0;

		if (start.isAfter(other.end)) return 0;
		if (end.isBefore(other.start)) return 0;

		if (start.isAfterOrSame(other.start) && end.isBeforeOrSame(other.end)) return getDayCount();

		int daysBefore = Math.max(0, start.getPeriodTo(other.start).toDays());
		int daysAfter = Math.max(0, other.end.getPeriodTo(end).toDays());

		return getDayCount() - daysBefore - daysAfter;
	}

	/**
	 * @return partial ofthe overlapping days of this DateRange with the given DateRange. It would be 0.5 if
	 *         half of this DateRange overlaps with the given DateRange.
	 */
	public double getOverlappingDaysAsPartial(DateRange other) {
		int overlappingDays = getOverlappingDays(other);
		if (overlappingDays == 0) return 0;
		int dayCount = getDayCount();
		return (double) overlappingDays / (double) dayCount;
	}

	/**
	 * @return true, if this DateRange contains at least one day of the given DateRange
	 */
	public boolean containsAny(DateRange other) {
		if (other == null) return false;
		if (other.isOneDay()) return contains(other.start);
		if (other.start.isAfter(end)) return false;
		if (other.end.isBefore(start)) return false;
		return true;
	}

	/**
	 * @return true, if tis DateRange contains all days of the given DateRange
	 */
	public boolean containsAll(DateRange other) {
		if (other == null) return false;
		if (other.isOneDay()) return contains(other.start);
		return start.isBeforeOrSame(other.start) && end.isAfterOrSame(other.end);
	}

	/**
	 * @return true, if this DateRange is completely contained in the given DateRange
	 */
	public boolean isContainedIn(DateRange other) {
		if (other == null) return false;
		return other.containsAll(this);
	}

	/**
	 * @return true, if this DateRange is overlapping the given DateRange at least one day (which could be the
	 *         start of one of them and the end of the other)
	 */
	public boolean isOverlapping(DateRange other) {
		if (other == null) return false;
		return containsAny(other);
	}

	public DateRange moveToYear(int year) {
		return new DateRange(new Date(year, start.month, start.day), new Date(year, end.month, end.day));
	}

	// --- creates ---

	public static DateRange nextYear() {
		return year(Tm.getCurrentYear() + 1);
	}

	public static DateRange currentYear() {
		return year(Tm.getCurrentYear());
	}

	public static DateRange year(int year) {
		return new DateRange(new Date(year, 1, 1), new Date(year, 12, 31));
	}

	public static DateRange currentMonth() {
		return monthOf(Date.today());
	}

	public static DateRange previousMonth() {
		return monthOf(Date.today().addMonths(-1));
	}

	public static DateRange monthOf(Date day) {
		return new DateRange(day.getFirstDateOfMonth(), day.getLastDateOfMonth());
	}

	public static DateRange monthOf(int year, int month) {
		return monthOf(new Date(year, month, 1));
	}

	// --- utils ---

	public static Date getStart(DateRange dateRange) {
		return dateRange == null ? null : dateRange.start;
	}

	public static Date getEnd(DateRange dateRange) {
		return dateRange == null ? null : dateRange.end;
	}

}