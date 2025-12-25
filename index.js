const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// DATABASE SEMENTARA (Array)
// Data akan hilang jika server dimatikan. 
// Jika ingin permanen, butuh database file/SQL, tapi untuk UAS ini sudah cukup memenuhi syarat REST API.
let tasks = [];

// Helper: Format Tanggal (YYYY-MM-DD HH:mm:ss)
const getCurrentTime = () => {
  const now = new Date();
  return now.toISOString().replace('T', ' ').substring(0, 19);
};

// ---------------- ROUTES (Jalan Raya API) ----------------

// 1. GET ALL TASKS
app.get('/tasks', (req, res) => {
  res.json(tasks);
});

// 2. CREATE NEW TASK
app.post('/tasks', (req, res) => {
  const { title, description, category } = req.body;

  const newTask = {
    id: Date.now().toString(), // Generate ID unik pakai waktu
    title: title,
    description: description,
    category: category,      // Normal, Urgent, Important
    status: "New",           // Default status
    created_time: getCurrentTime(),
    finished_time: null,
    duration: null
  };

  tasks.push(newTask);
  console.log(`[CREATED] Task: ${title}`);
  res.status(201).json(newTask);
});

// 3. UPDATE TASK STATUS & LOGIC DURATION
app.put('/tasks/:id', (req, res) => {
  const { id } = req.params;
  const { status } = req.body; // Status baru yang dikirim Android

  // Cari task berdasarkan ID
  const taskIndex = tasks.findIndex(t => t.id === id);

  if (taskIndex > -1) {
    // Update Status
    tasks[taskIndex].status = status;

    // LOGIC: Jika status berubah jadi "Done", hitung durasi
    if (status === "Done") {
      tasks[taskIndex].finished_time = getCurrentTime();

      // Hitung selisih waktu (Duration)
      const start = new Date(tasks[taskIndex].created_time);
      const end = new Date(tasks[taskIndex].finished_time);

      // Hitung selisih dalam menit
      const diffMs = end - start;
      const diffMins = Math.floor(diffMs / 60000);
      const diffHours = Math.floor(diffMins / 60);
      const remainingMins = diffMins % 60;

      tasks[taskIndex].duration = `${diffHours} Jam ${remainingMins} Menit`;
    }

    console.log(`[UPDATED] Task ID ${id} -> ${status}`);
    res.json(tasks[taskIndex]);
  } else {
    res.status(404).json({ message: "Task not found" });
  }
});

// Jalankan Server
app.listen(PORT, () => {
  console.log(`Server berjalan di http://localhost:${PORT}`);
});