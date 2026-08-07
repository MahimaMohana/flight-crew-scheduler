/**
 * Crew portal calendar view.
 *
 * On load: resolves the logged-in crew member (/api/auth/me), then renders a
 * month grid and fills it from /api/trip-assignments/my-calendar for the
 * visible date range. Each assignment is rendered as a "boarding-stub" chip
 * on every day its trip spans.
 */

const DOW_LABELS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const MONTH_LABELS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

const state = {
  viewYear: new Date().getFullYear(),
  viewMonth: new Date().getMonth() // 0-indexed
};

function toIsoDate(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

function isSameDay(a, b) {
  return a.getFullYear() === b.getFullYear()
    && a.getMonth() === b.getMonth()
    && a.getDate() === b.getDate();
}

async function fetchCurrentCrewMember() {
  const response = await fetch('/api/auth/me');
  if (!response.ok) {
    window.location.href = '/login.html';
    return null;
  }
  return response.json();
}

async function fetchCalendarEvents(rangeStart, rangeEnd) {
  const url = `/api/trip-assignments/my-calendar?start=${toIsoDate(rangeStart)}&end=${toIsoDate(rangeEnd)}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error('Failed to load calendar events', response.status);
    return [];
  }
  return response.json();
}

function renderCrewBadge(crewMember) {
  document.getElementById('crewName').textContent = `${crewMember.firstName} ${crewMember.lastName}`;
  document.getElementById('crewMeta').textContent =
    `${crewMember.employeeId} \u00B7 Base ${crewMember.baseAirport}`;
  document.getElementById('crewRole').textContent = crewMember.crewRole.replace('_', ' ');
}

function buildGridDays(year, month) {
  const firstOfMonth = new Date(year, month, 1);
  const startOffset = firstOfMonth.getDay(); // 0=Sun
  const gridStart = new Date(year, month, 1 - startOffset);

  const days = [];
  for (let i = 0; i < 42; i++) {
    const day = new Date(gridStart);
    day.setDate(gridStart.getDate() + i);
    days.push(day);
  }
  return days;
}

function eventsForDay(events, day) {
  return events.filter(event => {
    const start = new Date(event.start);
    const end = new Date(event.end); // exclusive
    return day >= stripTime(start) && day < stripTime(end);
  });
}

function stripTime(date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate());
}

function renderCalendar(events) {
  const grid = document.getElementById('calendarGrid');
  grid.innerHTML = '';

  DOW_LABELS.forEach(label => {
    const el = document.createElement('div');
    el.className = 'dow-label';
    el.textContent = label;
    grid.appendChild(el);
  });

  const today = new Date();
  const days = buildGridDays(state.viewYear, state.viewMonth);

  days.forEach(day => {
    const cell = document.createElement('div');
    const isOutside = day.getMonth() !== state.viewMonth;
    cell.className = 'day-cell' + (isOutside ? ' outside' : '') + (isSameDay(day, today) ? ' today' : '');

    const dayNumber = document.createElement('div');
    dayNumber.className = 'day-number';
    dayNumber.textContent = String(day.getDate());
    cell.appendChild(dayNumber);

    eventsForDay(events, day).forEach(event => {
      const chip = document.createElement('div');
      chip.className = 'assignment-chip';
      chip.style.background = event.color;
      chip.title = `${event.tripNumber} \u00B7 ${event.assignmentRole} \u00B7 ${event.status}`;

      const tripLine = document.createElement('span');
      tripLine.className = 'chip-trip';
      tripLine.textContent = event.tripNumber;
      chip.appendChild(tripLine);

      const roleLine = document.createElement('span');
      roleLine.className = 'chip-role';
      roleLine.textContent = event.assignmentRole.replace('_', ' ');
      chip.appendChild(roleLine);

      cell.appendChild(chip);
    });

    grid.appendChild(cell);
  });

  document.getElementById('monthLabel').textContent = `${MONTH_LABELS[state.viewMonth]} ${state.viewYear}`;
}

async function loadAndRenderMonth() {
  const gridDays = buildGridDays(state.viewYear, state.viewMonth);
  const rangeStart = gridDays[0];
  const rangeEnd = gridDays[gridDays.length - 1];

  const events = await fetchCalendarEvents(rangeStart, rangeEnd);
  renderCalendar(events);
}

function wireNavigation() {
  document.getElementById('prevMonth').addEventListener('click', () => {
    state.viewMonth -= 1;
    if (state.viewMonth < 0) {
      state.viewMonth = 11;
      state.viewYear -= 1;
    }
    loadAndRenderMonth();
  });

  document.getElementById('nextMonth').addEventListener('click', () => {
    state.viewMonth += 1;
    if (state.viewMonth > 11) {
      state.viewMonth = 0;
      state.viewYear += 1;
    }
    loadAndRenderMonth();
  });

  document.getElementById('todayBtn').addEventListener('click', () => {
    const now = new Date();
    state.viewYear = now.getFullYear();
    state.viewMonth = now.getMonth();
    loadAndRenderMonth();
  });
}

(async function init() {
  const crewMember = await fetchCurrentCrewMember();
  if (!crewMember) {
    return;
  }
  renderCrewBadge(crewMember);
  wireNavigation();
  await loadAndRenderMonth();
})();
