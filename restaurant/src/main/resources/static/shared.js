// ============================================================
// SAVOR — Shared Utilities
// ============================================================
const BASE = 'http://localhost:8080';

// --- Storage helpers ---
const Store = {
  set: (k, v) => localStorage.setItem(k, JSON.stringify(v)),
  get: (k) => {
    try {
      return JSON.parse(localStorage.getItem(k));
    } catch {
      return null;
    }
  },
  clear: () => localStorage.clear()
};

function setUser(u) {
  Store.set('savor_user', u);
}

function getUser() {
  return Store.get('savor_user');
}





// --- HTTP helpers ---
async function api(method, path, body = null) {
  try {
    const opts = {
      method,
      headers: {
        'Content-Type': 'application/json'
      }
    };

    if (body) {
      opts.body = JSON.stringify(body);
    }

    const res = await fetch(BASE + path, opts);

    console.log("API URL:", BASE + path);
    console.log("STATUS:", res.status);

    const data = await res.json();

    console.log("RESPONSE:", data);

    return {
      ok: res.ok,
      status: res.status,
      data
    };
  } catch (err) {
    console.error("API ERROR:", err);
    return {
      ok: false,
      status: 500,
      data: { message: err.message }
    };
  }
}

// --- Auth helpers ---
function getUser() { return Store.get('savor_user'); }
function setUser(u) { Store.set('savor_user', u); }
function logout() { Store.clear(); window.location.href = 'login.html'; }
function requireAuth() {
  if (!getUser()) { window.location.href = 'login.html'; return false; }
  return true;
}

// --- Toast notifications ---
function showToast(msg, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.style.cssText = 'position:fixed;top:1.5rem;right:1.5rem;z-index:9999;display:flex;flex-direction:column;gap:0.5rem';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  const colors = { success: '#166534,#D1FAE5', error: '#991B1B,#FEE2E2', info: '#1E40AF,#DBEAFE', warning: '#92400E,#FEF3C7' };
  const [color, bg] = (colors[type] || colors.info).split(',');
  toast.style.cssText = `background:${bg};color:${color};padding:0.75rem 1.2rem;border-radius:4px;font-size:0.88rem;font-family:'DM Sans',sans-serif;box-shadow:0 4px 20px rgba(0,0,0,0.12);max-width:320px;animation:slideIn 0.3s ease`;
  toast.textContent = msg;
  container.appendChild(toast);
  setTimeout(() => { toast.style.opacity = '0'; toast.style.transition = 'opacity 0.3s'; setTimeout(() => toast.remove(), 300); }, 3000);
}

// --- Render user nav state ---
function renderNavUser() {
  const user = getUser();
  const el = document.getElementById('nav-user-area');
  if (!el) return;
  if (user) {
    el.innerHTML = `
      <span style="font-size:0.85rem;color:var(--warm-gray)">Hi, ${user.name?.split(' ')[0]}</span>
      ${user.role === 'RESTAURANT_ADMIN' || user.role === 'ADMIN' ? '<a href="admin.html" style="color:var(--amber);font-size:0.85rem;text-decoration:none;font-weight:500">Dashboard</a>' : ''}
      <button onclick="logout()" style="background:transparent;border:1.5px solid rgba(200,118,42,0.3);color:var(--amber);padding:0.45rem 1rem;border-radius:2px;cursor:pointer;font-family:\'DM Sans\',sans-serif;font-size:0.82rem">Logout</button>`;
  } else {
    el.innerHTML = `<a href="login.html" style="background:var(--dark);color:var(--cream);padding:0.6rem 1.4rem;border-radius:2px;font-size:0.85rem;text-decoration:none;font-weight:500">Sign In</a>`;
  }
}

// --- Shared nav HTML ---
function getNav(activePage) {
  const pages = [
    ['index.html', 'Home'],
    ['restaurants.html', 'Restaurants'],
    ['services.html', 'Services'],
    ['about.html', 'About'],
    ['contact.html', 'Contact']
  ];
  return `
  <nav style="position:sticky;top:0;z-index:100;padding:1.1rem 3rem;display:flex;justify-content:space-between;align-items:center;background:rgba(253,246,236,0.95);backdrop-filter:blur(12px);border-bottom:1px solid rgba(200,118,42,0.15)">
    <a href="index.html" style="font-family:'Playfair Display',serif;font-size:1.6rem;font-weight:900;color:#1A1008;text-decoration:none">Sav<span style="color:#C8762A">or</span></a>
    <ul style="display:flex;gap:2rem;list-style:none">
      ${pages.map(([href, label]) => `<li><a href="${href}" style="text-decoration:none;color:${activePage===href?'#C8762A':'#8A7A6A'};font-size:0.88rem;font-weight:500;letter-spacing:0.04em;text-transform:uppercase">${label}</a></li>`).join('')}
    </ul>
    <div id="nav-user-area" style="display:flex;align-items:center;gap:1rem"></div>
  </nav>`;
}

function getFooter() {
  return `
  <footer style="
      background:#120C04;
      padding:1.8rem 3rem;
      border-top:1px solid rgba(255,255,255,0.08);
  ">
      <div style="
          display:flex;
          justify-content:space-between;
          align-items:flex-start;
          flex-wrap:wrap;
          gap:2rem;
      ">

          <div>
              <div style="
                  font-family:'Playfair Display',serif;
                  font-size:1.5rem;
                  font-weight:900;
                  color:#FDF6EC;
                  margin-bottom:0.8rem;
              ">
                  Sav<span style="color:#C8762A">or</span>
              </div>

              <p style="
                  color:rgba(253,246,236,0.55);
                  max-width:380px;
                  line-height:1.6;
                  font-size:0.9rem;
              ">
                  A modern restaurant management platform designed to streamline
                  restaurant operations, menu management, customer engagement,
                  order tracking, and delivery coordination.
              </p>
          </div>

          <div>
              <h4 style="
                  color:#FDF6EC;
                  margin-bottom:0.8rem;
                  font-size:1rem;
                  font-weight:600;
              ">
                  Contact
              </h4>

              <p style="
                  color:rgba(253,246,236,0.55);
                  line-height:1.8;
                  font-size:0.9rem;
                  margin:0;
              ">
                  Bangalore, Karnataka, India<br>
                  support@savor.com<br>
                  +91 96937 11738
              </p>
          </div>

      </div>

      <div style="
          margin-top:1rem;
          padding-top:0.8rem;
          border-top:1px solid rgba(255,255,255,0.08);
          text-align:center;
          color:rgba(253,246,236,0.35);
          font-size:0.82rem;
      ">
          © 2026 Savor Restaurant Management System. All Rights Reserved.
      </div>
  </footer>`;
}

const slideInStyle = `<style>@keyframes slideIn{from{opacity:0;transform:translateX(20px)}to{opacity:1;transform:translateX(0)}}</style>`;