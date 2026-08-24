/**
 * ============================================================================
 * DOCTOR DASHBOARD JAVASCRIPT CONTROLLER (100% UNICODE-SAFE ESCAPED)
 * ============================================================================
 */

function openMedicalModal(id, patientName, serviceName, diagnosis, prescription, moistureVal, oilVal) {
  const elId = document.getElementById("modalAppointmentId");
  const elName = document.getElementById("modalPatientName");
  const elService = document.getElementById("modalServiceName");
  const elDiag = document.getElementById("modalDiagnosis");
  const elPresc = document.getElementById("modalPrescription");

  if (elId) elId.value = id;
  if (elName) elName.innerText = patientName;
  if (elService) elService.innerText = serviceName;
  if (elDiag) elDiag.value = diagnosis || "";
  if (elPresc) elPresc.value = prescription || "";

  // Khởi tạo chỉ số da từ dữ liệu thật hoặc mặc định
  const moisture =
    moistureVal !== null && moistureVal !== undefined ? moistureVal : Math.floor(Math.random() * 25) + 50;
  const oil = oilVal !== null && oilVal !== undefined ? oilVal : Math.floor(Math.random() * 30) + 40;
  const elMoist = document.getElementById("moistureSlider");
  const elMoistVal = document.getElementById("moistureVal");
  const elOil = document.getElementById("oilSlider");
  const elOilVal = document.getElementById("oilVal");

  if (elMoist) elMoist.value = moisture;
  if (elMoistVal) elMoistVal.innerText = moisture + "%";
  if (elOil) elOil.value = oil;
  if (elOilVal) elOilVal.innerText = oil + "%";

  const modalEl = document.getElementById("medicalModal");
  if (modalEl && typeof bootstrap !== "undefined") {
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
  }
}

// ============================================================================
// DOCTOR AJAX SCHEDULE MANAGEMENT
// ============================================================================

function renderDoctorSlots(slots) {
  const container = document.getElementById("doctorSlotsContainer");
  if (!container) return;
  if (!slots || slots.length === 0) {
    container.innerHTML =
      '<div class="text-center py-4 text-muted"><i class="fa-solid fa-calendar-xmark me-2"></i>B\u00e1c s\u0129 ch\u01b0a c\u00f3 khung gi\u1edd l\u00e0m vi\u1ec7c n\u00e0o cho ng\u00e0y n\u00e0y.<button type="button" class="btn btn-sm btn-outline-info rounded-pill px-3 py-1 ms-2" onclick="generateScheduleAjax()"><i class="fa-solid fa-wand-magic-sparkles me-1"></i>T\u1ef1 \u0110\u1ed9ng Sinh T\u1ea5t C\u1ea3 Ca Kh\u00e1m</button></div>';
    return;
  }
  let html = '<div class="row g-3 animate-fade-in">';
  slots.forEach((slot) => {
    const isAvail = slot.isAvailable === true || slot.isAvailable === 1;
    const borderClass = isAvail ? "border-cyan bg-cyan bg-opacity-10" : "border-secondary bg-dark text-muted";
    const clockIcon = isAvail ? "text-cyan" : "text-muted";
    const textClass = isAvail ? "text-white" : "text-muted";
    const statusBadge = isAvail ? "\uD83D\uDFE2 Kh\u1ea3 d\u1ee5ng" : "\uD83D\uDD34 Kh\u00f3a / \u0110\u00e3 \u0111\u1eb7t";
    const lockBtnClass = isAvail ? "btn-outline-warning" : "btn-outline-success";
    const lockIcon = isAvail ? "fa-lock" : "fa-unlock";
    const lockTitle = isAvail ? "Kh\u00f3a ca n\u00e0y" : "M\u1edf ca n\u00e0y";

    html +=
      '<div class="col-12 col-sm-6 col-md-4 col-xl-3">' +
      '<div class="p-3 rounded-3 border d-flex align-items-center justify-content-between gap-2 ' +
      borderClass +
      ' shadow-sm" style="transition:all 0.2s;">' +
      '<div class="d-flex align-items-center gap-2">' +
      '<i class="fa-solid fa-clock fs-5 ' +
      clockIcon +
      '"></i>' +
      "<div>" +
      '<div class="fw-bold fs-7 ' +
      textClass +
      '">' +
      slot.startTime +
      " - " +
      slot.endTime +
      "</div>" +
      '<div class="fs-8 ' +
      (isAvail ? "text-emerald fw-semibold" : "text-danger") +
      '">' +
      statusBadge +
      "</div>" +
      "</div>" +
      "</div>" +
      '<div class="d-flex gap-1">' +
      '<button type="button" class="btn btn-xs ' +
      lockBtnClass +
      ' p-1 rounded-circle" title="' +
      lockTitle +
      '" onclick="confirmToggleSlot(' +
      slot.id +
      ", " +
      !isAvail +
      ", '" +
      slot.startTime +
      " - " +
      slot.endTime +
      "')\">" +
      '<i class="fa-solid ' +
      lockIcon +
      '"></i>' +
      "</button>" +
      '<button type="button" class="btn btn-xs btn-outline-danger p-1 rounded-circle" title="X\u00f3a ca n\u00e0y" onclick="deleteSlotAjax(' +
      slot.id +
      ')">' +
      '<i class="fa-solid fa-trash"></i>' +
      "</button>" +
      "</div>" +
      "</div>" +
      "</div>";
  });
  html += "</div>";
  container.innerHTML = html;
}

function fetchDoctorSlotsAjax() {
  const ctx = window.DOCTOR_CTX || "";
  const date = window.DOCTOR_CURRENT_DATE || "";
  const params = new URLSearchParams();
  params.append("action", "get-slots");
  params.append("date", date);
  params.append("ajax", "true");

  fetch(ctx + "/doctor/dashboard", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: params,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.slots) renderDoctorSlots(data.slots);
    })
    .catch((err) => console.error("AJAX Error:", err));
}

function generateScheduleAjax() {
  const ctx = window.DOCTOR_CTX || "";
  const date = window.DOCTOR_CURRENT_DATE || "";
  const params = new URLSearchParams();
  params.append("action", "generate-schedule");
  params.append("date", date);
  params.append("ajax", "true");

  fetch(ctx + "/doctor/dashboard", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: params,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.slots) renderDoctorSlots(data.slots);
      showToastNotification(data.success ? "success" : "error", data.message);
    })
    .catch((err) => console.error("AJAX Error:", err));
}

function confirmToggleSlot(slotId, newStatus, slotTime) {
  const actionText = newStatus ? "M\u1edf Ca" : "Kh\u00f3a Ca";
  const actionLower = newStatus ? "m\u1edf ca" : "kh\u00f3a ca";
  const confirmColor = newStatus ? "#10b981" : "#f59e0b";
  const timeLabel = slotTime ? "khung gi\u1edd " + slotTime : "ca kh\u00e1m n\u00e0y";

  if (typeof Swal !== "undefined") {
    Swal.fire({
      title: "X\u00e1c Nh\u1eadn " + actionText,
      html: "B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n <b>" + actionLower + "</b> " + timeLabel + " kh\u00f4ng?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: confirmColor,
      cancelButtonColor: "#475569",
      confirmButtonText: '<i class="fa-solid fa-check me-1"></i> ' + actionText,
      cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> H\u1ee7y B\u1ecf',
      background: "#0b1628",
      color: "#ffffff",
      customClass: {
        popup: "glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg",
      },
    }).then((result) => {
      if (result.isConfirmed) {
        toggleSlotAjax(slotId, newStatus);
      }
    });
  } else {
    if (confirm("B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n " + actionLower + " " + timeLabel + " kh\u00f4ng?")) {
      toggleSlotAjax(slotId, newStatus);
    }
  }
}

function toggleSlotAjax(slotId, newStatus) {
  const ctx = window.DOCTOR_CTX || "";
  const date = window.DOCTOR_CURRENT_DATE || "";
  const params = new URLSearchParams();
  params.append("action", "toggle-slot");
  params.append("slotId", slotId);
  params.append("status", newStatus);
  params.append("date", date);
  params.append("ajax", "true");

  fetch(ctx + "/doctor/dashboard", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: params,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.slots) renderDoctorSlots(data.slots);
      showToastNotification(data.success ? "success" : "error", data.message);
    })
    .catch((err) => console.error("AJAX Error:", err));
}

function deleteSlotAjax(slotId) {
  const ctx = window.DOCTOR_CTX || "";
  const date = window.DOCTOR_CURRENT_DATE || "";

  if (typeof Swal !== "undefined") {
    Swal.fire({
      title: "X\u00f3a Ca Kh\u00e1m N\u00e0y?",
      text: "B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n x\u00f3a khung gi\u1edd kh\u00e1m n\u00e0y kh\u00f4ng?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#ef4444",
      cancelButtonColor: "#64748b",
      confirmButtonText: '<i class="fa-solid fa-trash me-1"></i> X\u00f3a Ca',
      cancelButtonText: "H\u1ee7y B\u1ecf",
      background: "#0f172a",
      color: "#f8fafc",
      customClass: {
        popup: "border border-danger border-opacity-40 shadow-lg",
      },
    }).then((result) => {
      if (result.isConfirmed) {
        performDeleteSlot(ctx, date, slotId);
      }
    });
  } else {
    if (confirm("B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n x\u00f3a khung gi\u1edd kh\u00e1m n\u00e0y kh\u00f4ng?")) {
      performDeleteSlot(ctx, date, slotId);
    }
  }
}

function performDeleteSlot(ctx, date, slotId) {
  const params = new URLSearchParams();
  params.append("action", "delete-slot");
  params.append("slotId", slotId);
  params.append("date", date);
  params.append("ajax", "true");

  fetch(ctx + "/doctor/dashboard", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: params,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.slots) renderDoctorSlots(data.slots);
      showToastNotification(data.success ? "success" : "error", data.message);
    })
    .catch((err) => console.error("AJAX Error:", err));
}

// ============================================================================
// SINGLE SLOT & WEEKLY BATCH SCHEDULE AJAX
// ============================================================================

function addSlotAjax(event) {
  if (event) event.preventDefault();
  const form = document.getElementById("addSlotForm");
  if (!form) return;

  const ctx = window.DOCTOR_CTX || "";
  const formData = new FormData(form);
  const params = new URLSearchParams();
  for (const [key, value] of formData.entries()) {
    params.append(key, value);
  }
  params.set("ajax", "true");

  fetch(ctx + "/doctor/dashboard", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: params,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.slots) renderDoctorSlots(data.slots);
      showToastNotification(data.success ? "success" : "error", data.message);
      const modalEl = document.getElementById("addSlotModal");
      if (modalEl && typeof bootstrap !== "undefined") {
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) modal.hide();
      }
    })
    .catch((err) => {
      console.error("AJAX Error:", err);
      showToastNotification("error", "L\u1ed7i khi th\u00eam ca kh\u00e1m!");
    });
}

function submitWeeklyScheduleAjax(event) {
  if (event) event.preventDefault();
  const form = document.getElementById("weeklyScheduleForm");
  if (!form) return;

  const ctx = window.DOCTOR_CTX || "";
  const formData = new FormData(form);

  const startDate = formData.get("startDate");
  const endDate = formData.get("endDate");
  const days = formData.getAll("daysOfWeek");
  const slots = formData.getAll("timeSlots");

  if (!startDate || !endDate) {
    showToastNotification("error", "Vui l\u00f2ng ch\u1ecdn ng\u00e0y b\u1eaft \u0111\u1ea7u v\u00e0 k\u1ebft th\u00fac!");
    return;
  }
  if (days.length === 0) {
    showToastNotification("error", "Vui l\u00f2ng ch\u1ecdn \u00edt nh\u1ea5t m\u1ed9t ng\u00e0y l\u00e0m vi\u1ec7c trong tu\u1ea7n!");
    return;
  }
  if (slots.length === 0) {
    showToastNotification("error", "Vui l\u00f2ng ch\u1ecdn \u00edt nh\u1ea5t m\u1ed9t khung gi\u1edd kh\u00e1m!");
    return;
  }

  const params = new URLSearchParams();
  params.append("action", "register-weekly-schedule");
  params.append("startDate", startDate);
  params.append("endDate", endDate);
  params.append("ajax", "true");
  days.forEach((d) => params.append("daysOfWeek", d));
  slots.forEach((s) => params.append("timeSlots", s));

  const btnSubmit = document.getElementById("btnSubmitWeekly");
  if (btnSubmit) {
    btnSubmit.disabled = true;
    btnSubmit.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-1"></i>\u0110ang X\u1eed L\u00fd...';
  }

  fetch(ctx + "/doctor/dashboard", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: params,
  })
    .then((res) => res.json())
    .then((data) => {
      if (btnSubmit) {
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = '<i class="fa-solid fa-wand-magic-sparkles me-1"></i>\u00c1p D\u1ee5ng L\u1ecbch To\u00e0n Tu\u1ea7n';
      }

      if (data.success) {
        if (typeof Swal !== "undefined") {
          Swal.fire({
            title: "Th\u00e0nh C\u00f4ng!",
            text: data.message,
            icon: "success",
            confirmButtonColor: "#0ea5e9",
            background: "#0b1628",
            color: "#ffffff",
          });
        } else {
          showToastNotification("success", data.message);
        }
        const modalEl = document.getElementById("weeklyScheduleModal");
        if (modalEl && typeof bootstrap !== "undefined") {
          const modal = bootstrap.Modal.getInstance(modalEl);
          if (modal) modal.hide();
        }
        fetchDoctorSlotsAjax();
      } else {
        showToastNotification("error", data.message || "Kh\u00f4ng th\u1ec3 \u0111\u0103ng k\u00fd l\u1ecbch!");
      }
    })
    .catch((err) => {
      console.error("AJAX Error:", err);
      if (btnSubmit) {
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = '<i class="fa-solid fa-wand-magic-sparkles me-1"></i>\u00c1p D\u1ee5ng L\u1ecbch To\u00e0n Tu\u1ea7n';
      }
      showToastNotification("error", "L\u1ed7i k\u1ebft n\u1ed1i khi \u0111\u0103ng k\u00fd l\u1ecbch l\u00e0m vi\u1ec7c!");
    });
}

function clearWeeklyScheduleAjax() {
  const form = document.getElementById("weeklyScheduleForm");
  if (!form) return;

  const ctx = window.DOCTOR_CTX || "";
  const formData = new FormData(form);
  const startDate = formData.get("startDate");
  const endDate = formData.get("endDate");
  const days = formData.getAll("daysOfWeek");

  if (!startDate || !endDate) {
    showToastNotification("error", "Vui l\u00f2ng ch\u1ecdn ng\u00e0y b\u1eaft \u0111\u1ea7u v\u00e0 k\u1ebft th\u00fac!");
    return;
  }

  const doClear = () => {
    const params = new URLSearchParams();
    params.append("action", "clear-weekly-schedule");
    params.append("startDate", startDate);
    params.append("endDate", endDate);
    params.append("ajax", "true");
    days.forEach((d) => params.append("daysOfWeek", d));

    fetch(ctx + "/doctor/dashboard", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
      body: params,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          showToastNotification("success", data.message);
          const modalEl = document.getElementById("weeklyScheduleModal");
          if (modalEl && typeof bootstrap !== "undefined") {
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();
          }
          fetchDoctorSlotsAjax();
        } else {
          showToastNotification("error", data.message || "Kh\u00f4ng th\u1ec3 d\u1ecdn d\u1eb9p ca kh\u00e1m!");
        }
      })
      .catch((err) => {
        console.error("AJAX Error:", err);
        showToastNotification("error", "L\u1ed7i khi d\u1ecdn d\u1eb9p ca kh\u00e1m!");
      });
  };

  if (typeof Swal !== "undefined") {
    Swal.fire({
      title: "D\u1ecdn D\u1eb9p Ca Kh\u00e1m Tr\u1ed1ng?",
      text: "H\u1ec7 th\u1ed1ng s\u1ebd x\u00f3a c\u00e1c ca kh\u00e1m ch\u01b0a c\u00f3 ng\u01b0\u1eddi \u0111\u1eb7t trong kho\u1ea3ng ng\u00e0y \u0111\u00e3 ch\u1ecdn. Ca \u0111\u00e3 c\u00f3 b\u1ec7nh nh\u00e2n \u0111\u1eb7t l\u1ecbch s\u1ebd \u0111\u01b0\u1ee3c gi\u1eef nguy\u00ean an to\u00e0n.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#ef4444",
      cancelButtonColor: "#64748b",
      confirmButtonText: '<i class="fa-solid fa-broom me-1"></i> D\u1ecdn D\u1eb9p Ngay',
      cancelButtonText: "H\u1ee7y B\u1ecf",
      background: "#0b1628",
      color: "#ffffff",
    }).then((result) => {
      if (result.isConfirmed) {
        doClear();
      }
    });
  } else {
    if (confirm("B\u1ea1n c\u00f3 ch\u1eafc mu\u1ed1n d\u1ecdn d\u1eb9p c\u00e1c ca kh\u00e1m tr\u1ed1ng trong kho\u1ea3ng ng\u00e0y \u0111\u00e3 ch\u1ecdn?")) {
      doClear();
    }
  }
}

// ============================================================================
// PRESET & UI HELPERS CHO MODAL LỊCH THEO TUẦN
// ============================================================================

function formatDateISO(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

function setWeeklyPreset(preset) {
  const today = new Date();
  const currentDay = today.getDay(); // 0: CN, 1: T2, ..., 6: T7
  const distanceToMonday = currentDay === 0 ? -6 : 1 - currentDay;

  const thisMonday = new Date(today);
  thisMonday.setDate(today.getDate() + distanceToMonday);

  let start = new Date();
  let end = new Date();

  if (preset === "this_week") {
    start = new Date(thisMonday);
    end = new Date(thisMonday);
    end.setDate(thisMonday.getDate() + 6);
  } else if (preset === "next_week") {
    start = new Date(thisMonday);
    start.setDate(thisMonday.getDate() + 7);
    end = new Date(start);
    end.setDate(start.getDate() + 6);
  } else if (preset === "next_2_weeks") {
    start = new Date(thisMonday);
    start.setDate(thisMonday.getDate() + 7);
    end = new Date(start);
    end.setDate(start.getDate() + 13);
  }

  const presetBtns = document.querySelectorAll('#weeklyScheduleModal .btn-group button');
  presetBtns.forEach(btn => {
    const fnAttr = btn.getAttribute('onclick') || '';
    if (fnAttr.includes(preset)) {
      btn.classList.add('active');
    } else if (fnAttr.includes('setWeeklyPreset')) {
      btn.classList.remove('active');
    }
  });

  const startInput = document.getElementById("weeklyStartDate");
  const endInput = document.getElementById("weeklyEndDate");
  if (startInput) startInput.value = formatDateISO(start);
  if (endInput) endInput.value = formatDateISO(end);

  calculateWeeklyEstimate();
}

function calculateWeeklyEstimate() {
  const startVal = document.getElementById("weeklyStartDate")?.value;
  const endVal = document.getElementById("weeklyEndDate")?.value;
  const countEl = document.getElementById("weeklyEstimatedCount");
  if (!countEl) return;

  if (!startVal || !endVal) {
    countEl.innerText = "0";
    return;
  }

  const start = new Date(startVal);
  const end = new Date(endVal);
  if (start > end) {
    countEl.innerText = "0";
    return;
  }

  const dayCheckboxes = document.querySelectorAll('input[name="daysOfWeek"]:checked');
  const selectedDays = Array.from(dayCheckboxes).map((cb) => parseInt(cb.value)); // 1..7 (1=Mon, 7=Sun)

  const slotCheckboxes = document.querySelectorAll('input[name="timeSlots"]:checked');
  const slotCount = slotCheckboxes.length;

  let matchingDays = 0;
  const cur = new Date(start);
  while (cur <= end) {
    let jsDay = cur.getDay(); // 0=Sun, 1=Mon...
    let appDay = jsDay === 0 ? 7 : jsDay;
    if (selectedDays.includes(appDay)) {
      matchingDays++;
    }
    cur.setDate(cur.getDate() + 1);
  }

  countEl.innerText = (matchingDays * slotCount).toString();
}

function syncAllChipsState() {
  document.querySelectorAll('input[name="daysOfWeek"]').forEach((cb) => {
    const label = cb.closest(".day-chip");
    if (label) {
      if (cb.checked) {
        label.classList.add("active");
        label.classList.remove("opacity-50");
      } else {
        label.classList.remove("active");
        label.classList.add("opacity-50");
      }
    }
  });
  document.querySelectorAll('input[name="timeSlots"]').forEach((cb) => {
    const label = cb.closest(".slot-chip");
    if (label) {
      if (cb.checked) {
        label.classList.add("active");
        label.classList.remove("opacity-50");
      } else {
        label.classList.remove("active");
        label.classList.add("opacity-50");
      }
    }
  });
  calculateWeeklyEstimate();
}

function handleDayChipChange(cb) {
  const label = cb.closest(".day-chip");
  if (label) {
    if (cb.checked) {
      label.classList.add("active");
      label.classList.remove("opacity-50");
    } else {
      label.classList.remove("active");
      label.classList.add("opacity-50");
    }
  }
  calculateWeeklyEstimate();
}

function handleSlotChipChange(cb) {
  const label = cb.closest(".slot-chip");
  if (label) {
    if (cb.checked) {
      label.classList.add("active");
      label.classList.remove("opacity-50");
    } else {
      label.classList.remove("active");
      label.classList.add("opacity-50");
    }
  }
  calculateWeeklyEstimate();
}

function selectWeekdayOnly() {
  document.querySelectorAll('input[name="daysOfWeek"]').forEach((cb) => {
    const val = parseInt(cb.value);
    cb.checked = val >= 1 && val <= 5;
    handleDayChipChange(cb);
  });
}

function toggleAllDays(check) {
  document.querySelectorAll('input[name="daysOfWeek"]').forEach((cb) => {
    cb.checked = check;
    handleDayChipChange(cb);
  });
}

function selectMorningSlots() {
  const morning = ["08:00", "09:00", "10:00", "11:00"];
  document.querySelectorAll('input[name="timeSlots"]').forEach((cb) => {
    cb.checked = morning.includes(cb.value.substring(0, 5));
    handleSlotChipChange(cb);
  });
}

function selectAfternoonSlots() {
  const afternoon = ["14:00", "15:00", "16:00", "17:00"];
  document.querySelectorAll('input[name="timeSlots"]').forEach((cb) => {
    cb.checked = afternoon.includes(cb.value.substring(0, 5));
    handleSlotChipChange(cb);
  });
}

function selectAllSlots(check) {
  document.querySelectorAll('input[name="timeSlots"]').forEach((cb) => {
    cb.checked = check;
    handleSlotChipChange(cb);
  });
}

function showToastNotification(type, message) {
  let container = document.getElementById("toastContainer");
  if (!container) {
    container = document.createElement("div");
    container.id = "toastContainer";
    document.body.appendChild(container);
  }
  const toast = document.createElement("div");
  toast.className = "toast-glass " + type;
  const iconClass =
    type === "success" ? "fa-solid fa-circle-check text-success" : "fa-solid fa-triangle-exclamation text-danger";
  toast.innerHTML =
    '<i class="' +
    iconClass +
    ' fs-5"></i><span style="font-size:.88rem; font-weight:600;">' +
    (message || "") +
    "</span>";
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateX(20px)";
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// Khởi tạo các giá trị khi trang được load
document.addEventListener("DOMContentLoaded", function () {
  syncAllChipsState();

  const weeklyModal = document.getElementById("weeklyScheduleModal");
  if (weeklyModal) {
    weeklyModal.addEventListener("show.bs.modal", function () {
      const startInput = document.getElementById("weeklyStartDate");
      if (startInput && !startInput.value) {
        setWeeklyPreset("next_week");
      }
      syncAllChipsState();
    });
  }
});
