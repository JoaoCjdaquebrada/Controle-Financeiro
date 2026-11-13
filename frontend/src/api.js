const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  });

  const text = await response.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    const message = data?.erro || data?.message || data?.detail || `Erro HTTP ${response.status}`;
    throw new Error(message);
  }

  return data;
}

export const api = {
  contas: () => request('/api/contas'),
  criarConta: (body) => request('/api/contas', { method: 'POST', body: JSON.stringify(body) }),
  excluirConta: (id) => request(`/api/contas/${id}`, { method: 'DELETE' }),

  categorias: () => request('/api/categorias'),
  criarCategoria: (body) => request('/api/categorias', { method: 'POST', body: JSON.stringify(body) }),

  lancamentos: () => request('/api/lancamentos'),
  criarLancamento: (body) => request('/api/lancamentos', { method: 'POST', body: JSON.stringify(body) }),
  excluirLancamento: (id) => request(`/api/lancamentos/${id}`, { method: 'DELETE' }),

  orcamentos: () => request('/api/orcamentos'),
  criarOrcamento: (body) => request('/api/orcamentos', { method: 'POST', body: JSON.stringify(body) }),
  excluirOrcamento: (id) => request(`/api/orcamentos/${id}`, { method: 'DELETE' }),
};
