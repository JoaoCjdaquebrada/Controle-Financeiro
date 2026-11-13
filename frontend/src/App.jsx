import { useCallback, useEffect, useMemo, useState } from 'react';
import { api } from './api';
import './App.css';

const money = (value) => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value || 0));
const localToday = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
};
const dateLabel = (value) => value ? new Date(`${value}T12:00:00`).toLocaleDateString('pt-BR') : '—';
const monthLabel = (month, year) => new Date(Number(year), Number(month) - 1, 1).toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' });

function Icon({ name }) {
  const paths = {
    grid: <><rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/></>,
    list: <><path d="M9 6h11M9 12h11M9 18h11"/><path d="M4 6h.01M4 12h.01M4 18h.01"/></>,
    wallet: <><path d="M4 6.5A2.5 2.5 0 0 1 6.5 4H20v16H6.5A2.5 2.5 0 0 1 4 17.5z"/><path d="M4 7h16M16 12h4"/><circle cx="16" cy="12" r=".7"/></>,
    tag: <><path d="M20.5 13 13 20.5 3.5 11V3.5H11z"/><circle cx="8" cy="8" r="1"/></>,
    target: <><circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5"/><circle cx="12" cy="12" r="1"/></>,
    plus: <><path d="M12 5v14M5 12h14"/></>,
    arrowUp: <><path d="m7 14 5-5 5 5"/><path d="M12 9v11"/></>,
    arrowDown: <><path d="m7 10 5 5 5-5"/><path d="M12 4v11"/></>,
    trend: <><path d="m3 17 6-6 4 4 8-9"/><path d="M15 6h6v6"/></>,
    refresh: <><path d="M20 7v5h-5"/><path d="M4 17v-5h5"/><path d="M5.5 9A7 7 0 0 1 18 6l2 6M4 12l2 6a7 7 0 0 0 12.5-3"/></>,
    trash: <><path d="M4 7h16M10 11v6M14 11v6M6 7l1 14h10l1-14M9 7V4h6v3"/></>,
    calendar: <><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18"/></>,
    menu: <><path d="M4 6h16M4 12h16M4 18h16"/></>,
  };
  return <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">{paths[name] || paths.grid}</svg>;
}

function App() {
  const [page, setPage] = useState('dashboard');
  const [data, setData] = useState({ contas: [], categorias: [], lancamentos: [], orcamentos: [] });
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [mobileMenu, setMobileMenu] = useState(false);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [contas, categorias, lancamentos, orcamentos] = await Promise.all([
        api.contas(), api.categorias(), api.lancamentos(), api.orcamentos(),
      ]);
      setData({ contas, categorias, lancamentos, orcamentos });
    } catch (err) {
      setError(`${err.message}. Confira se o backend Spring Boot está executando em http://localhost:8080 e se o CORS foi configurado.`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { refresh(); }, [refresh]);
  useEffect(() => {
    if (!notice) return undefined;
    const timer = window.setTimeout(() => setNotice(''), 3500);
    return () => window.clearTimeout(timer);
  }, [notice]);

  const today = new Date();
  const monthTransactions = useMemo(() => data.lancamentos.filter((item) => {
    const d = new Date(`${item.dataLancamento}T12:00:00`);
    return d.getMonth() === today.getMonth() && d.getFullYear() === today.getFullYear();
  }), [data.lancamentos, today]);

  const totals = useMemo(() => {
    const receitas = monthTransactions.filter((x) => x.tipo === 'RECEITA').reduce((sum, x) => sum + Number(x.valor), 0);
    const despesas = monthTransactions.filter((x) => x.tipo === 'DESPESA').reduce((sum, x) => sum + Number(x.valor), 0);
    const saldoContas = data.contas.reduce((sum, x) => sum + Number(x.saldoInicial), 0);
    const saldo = saldoContas + data.lancamentos.reduce((sum, x) => sum + (x.tipo === 'RECEITA' ? Number(x.valor) : -Number(x.valor)), 0);
    return { receitas, despesas, saldo, resultado: receitas - despesas };
  }, [data, monthTransactions]);

  const budgetRows = useMemo(() => data.orcamentos
    .filter((budget) => Number(budget.mes) === today.getMonth() + 1 && Number(budget.ano) === today.getFullYear())
    .map((budget) => {
      const spent = monthTransactions
        .filter((x) => x.tipo === 'DESPESA' && Number(x.categoriaId) === Number(budget.categoriaId))
        .reduce((sum, x) => sum + Number(x.valor), 0);
      const limit = Number(budget.limite);
      return { ...budget, spent, limit, percent: limit > 0 ? Math.min((spent / limit) * 100, 100) : 0 };
    }), [data.orcamentos, monthTransactions, today]);

  const runAction = async (action, successMessage) => {
    setBusy(true);
    setError('');
    try {
      await action();
      await refresh();
      setNotice(successMessage);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  const navItems = [
    { id: 'dashboard', label: 'Visão geral', icon: 'grid' },
    { id: 'lancamentos', label: 'Lançamentos', icon: 'list' },
    { id: 'contas', label: 'Contas', icon: 'wallet' },
    { id: 'categorias', label: 'Categorias', icon: 'tag' },
    { id: 'orcamentos', label: 'Orçamentos', icon: 'target' },
  ];
  const title = navItems.find((item) => item.id === page)?.label || 'Visão geral';

  return (
    <div className="app-shell">
      <aside className={`sidebar ${mobileMenu ? 'sidebar-open' : ''}`}>
        <div className="brand"><div className="brand-mark"><Icon name="wallet" /></div><div><strong>Organiza</strong><span>CONTROLE FINANCEIRO</span></div></div>
        <div className="sidebar-label">MENU PRINCIPAL</div>
        <nav>
          {navItems.map((item) => (
            <button key={item.id} className={`nav-item ${page === item.id ? 'active' : ''}`} onClick={() => { setPage(item.id); setMobileMenu(false); }}>
              <Icon name={item.icon} /><span>{item.label}</span>{page === item.id && <i />}
            </button>
          ))}
        </nav>
        <div className="sidebar-bottom"><div className="secure-dot" /><div><strong>Seus dados, organizados</strong><span>Conectado à sua API local</span></div></div>
      </aside>

      <main className="main-area">
        <header className="topbar">
          <button className="mobile-menu" onClick={() => setMobileMenu(!mobileMenu)} aria-label="Abrir menu"><Icon name="menu" /></button>
          <div><div className="eyebrow">PAINEL FINANCEIRO</div><h1>{title}</h1></div>
          <div className="top-actions"><div className="today-pill"><Icon name="calendar" />{today.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' })}</div><button className="icon-button" onClick={refresh} title="Atualizar dados"><Icon name="refresh" /></button></div>
        </header>

        {error && <div className="alert alert-error"><strong>Não foi possível carregar ou salvar os dados.</strong><span>{error}</span><button onClick={() => setError('')}>Fechar</button></div>}
        {notice && <div className="alert alert-success">{notice}</div>}

        {loading ? <div className="loading-card"><div className="spinner" />Carregando informações do seu financeiro...</div> : (
          <>
            {page === 'dashboard' && <Dashboard data={data} totals={totals} budgets={budgetRows} transactions={monthTransactions} setPage={setPage} />}
            {page === 'contas' && <AccountsPage contas={data.contas} busy={busy} runAction={runAction} />}
            {page === 'categorias' && <CategoriesPage categorias={data.categorias} busy={busy} runAction={runAction} />}
            {page === 'lancamentos' && <TransactionsPage data={data} busy={busy} runAction={runAction} />}
            {page === 'orcamentos' && <BudgetsPage data={data} rows={budgetRows} busy={busy} runAction={runAction} />}
          </>
        )}
        <footer className="footer"><span>Organiza · Controle Financeiro Pessoal</span><span>Dados sincronizados com seu backend Spring Boot</span></footer>
      </main>
    </div>
  );
}

function Dashboard({ data, totals, budgets, transactions, setPage }) {
  const latest = [...data.lancamentos].sort((a, b) => String(b.dataLancamento).localeCompare(String(a.dataLancamento)) || Number(b.id) - Number(a.id)).slice(0, 5);
  const max = Math.max(totals.receitas, totals.despesas, 1);
  return (
    <div className="page-content">
      <section className="welcome-row"><div><h2>Seu dinheiro sob controle.</h2><p>Acompanhe suas movimentações e veja como está seu mês.</p></div><button className="primary-button" onClick={() => setPage('lancamentos')}><Icon name="plus" /> Novo lançamento</button></section>
      <section className="summary-grid">
        <MetricCard label="Saldo total" value={money(totals.saldo)} hint="Saldo inicial + movimentações" icon="wallet" tone="navy" />
        <MetricCard label="Receitas do mês" value={money(totals.receitas)} hint="Tudo o que entrou" icon="arrowUp" tone="green" />
        <MetricCard label="Despesas do mês" value={money(totals.despesas)} hint="Tudo o que saiu" icon="arrowDown" tone="red" />
        <MetricCard label="Resultado do mês" value={money(totals.resultado)} hint="Receitas menos despesas" icon="trend" tone="purple" />
      </section>
      <section className="content-grid">
        <div className="panel overview-panel">
          <div className="panel-heading"><div><h3>Fluxo do mês</h3><p>Comparativo entre entradas e saídas</p></div><span className="period-tag">{monthLabel(new Date().getMonth() + 1, new Date().getFullYear())}</span></div>
          <div className="flow-chart">
            <div className="flow-item"><div className="flow-top"><span><i className="legend-dot green-dot" />Receitas</span><strong>{money(totals.receitas)}</strong></div><div className="bar-track"><div className="bar-fill green-fill" style={{ width: `${(totals.receitas / max) * 100}%` }} /></div></div>
            <div className="flow-item"><div className="flow-top"><span><i className="legend-dot red-dot" />Despesas</span><strong>{money(totals.despesas)}</strong></div><div className="bar-track"><div className="bar-fill red-fill" style={{ width: `${(totals.despesas / max) * 100}%` }} /></div></div>
          </div>
          <div className="chart-note"><span className="note-icon"><Icon name="trend" /></span><span>{totals.resultado >= 0 ? 'Seu mês está positivo. Continue acompanhando seus gastos.' : 'Suas despesas superaram as receitas neste mês.'}</span></div>
        </div>
        <div className="panel budget-panel"><div className="panel-heading"><div><h3>Orçamentos do mês</h3><p>Acompanhamento por categoria</p></div><button className="text-button" onClick={() => setPage('orcamentos')}>Ver todos</button></div>
          {budgets.length ? budgets.slice(0, 4).map((budget) => <BudgetProgress key={budget.id} budget={budget} />) : <Empty text="Nenhum orçamento cadastrado para este mês." action="Criar orçamento" onClick={() => setPage('orcamentos')} />}
        </div>
      </section>
      <section className="panel transactions-panel"><div className="panel-heading"><div><h3>Movimentações recentes</h3><p>Últimos registros do seu financeiro</p></div><button className="text-button" onClick={() => setPage('lancamentos')}>Ver lançamentos <span>→</span></button></div>
        <TransactionTable items={latest} />
      </section>
    </div>
  );
}

function MetricCard({ label, value, hint, icon, tone }) {
  return <div className={`metric-card metric-${tone}`}><div className="metric-top"><span>{label}</span><div className="metric-icon"><Icon name={icon} /></div></div><strong className="metric-value">{value}</strong><span className="metric-hint">{hint}</span></div>;
}

function BudgetProgress({ budget }) {
  const over = budget.spent > budget.limit;
  return <div className="budget-row"><div className="budget-row-top"><div><strong>{budget.categoriaNome}</strong><span>{money(budget.spent)} de {money(budget.limit)}</span></div><b className={over ? 'text-danger' : ''}>{Math.round(budget.limit ? (budget.spent / budget.limit) * 100 : 0)}%</b></div><div className="bar-track"><div className={`bar-fill ${over ? 'red-fill' : 'blue-fill'}`} style={{ width: `${budget.percent}%` }} /></div></div>;
}

function TransactionTable({ items, onDelete }) {
  if (!items.length) return <Empty text="Nenhuma movimentação encontrada." />;
  return <div className="table-wrap"><table><thead><tr><th>Descrição</th><th>Categoria</th><th>Data</th><th>Conta</th><th>Valor</th>{onDelete && <th />}</tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><div className="transaction-name"><span className={`transaction-symbol ${item.tipo === 'RECEITA' ? 'symbol-income' : 'symbol-expense'}`}><Icon name={item.tipo === 'RECEITA' ? 'arrowDown' : 'arrowUp'} /></span><div><strong>{item.descricao}</strong><small>{item.tipo === 'RECEITA' ? 'Receita' : 'Despesa'}</small></div></div></td><td>{item.categoriaNome || '—'}</td><td>{dateLabel(item.dataLancamento)}</td><td>{item.contaNome || '—'}</td><td className={`amount ${item.tipo === 'RECEITA' ? 'income' : 'expense'}`}>{item.tipo === 'RECEITA' ? '+' : '−'} {money(item.valor)}</td>{onDelete && <td><button className="delete-button" title="Excluir lançamento" onClick={() => onDelete(item.id)}><Icon name="trash" /></button></td>}</tr>)}</tbody></table></div>;
}

function Empty({ text, action, onClick }) {
  return <div className="empty-state"><span className="empty-icon">◇</span><p>{text}</p>{action && <button className="text-button" onClick={onClick}>{action}</button>}</div>;
}

function PageHeader({ title, description }) { return <div className="section-header"><div><h2>{title}</h2><p>{description}</p></div></div>; }

function AccountsPage({ contas, busy, runAction }) {
  const [form, setForm] = useState({ nome: '', tipoConta: 'CORRENTE', saldoInicial: '0.00' });
  const submit = (e) => { e.preventDefault(); runAction(() => api.criarConta({ ...form, saldoInicial: Number(form.saldoInicial) }), 'Conta cadastrada com sucesso!'); setForm({ nome: '', tipoConta: 'CORRENTE', saldoInicial: '0.00' }); };
  return <div className="page-content"><PageHeader title="Contas financeiras" description="Organize suas contas bancárias, carteiras e saldos iniciais." /><div className="split-layout"><form className="panel form-panel" onSubmit={submit}><h3>Adicionar conta</h3><p className="form-intro">Cadastre onde seu dinheiro está guardado.</p><Field label="Nome da conta"><input required maxLength="100" placeholder="Ex.: Conta Principal" value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} /></Field><Field label="Tipo de conta"><select value={form.tipoConta} onChange={(e) => setForm({ ...form, tipoConta: e.target.value })}><option value="CORRENTE">Conta corrente</option><option value="POUPANCA">Poupança</option><option value="CARTEIRA">Carteira</option><option value="OUTRA">Outra</option></select></Field><Field label="Saldo inicial (R$)"><input required type="number" min="0" step="0.01" value={form.saldoInicial} onChange={(e) => setForm({ ...form, saldoInicial: e.target.value })} /></Field><button className="primary-button full-button" disabled={busy}><Icon name="plus" /> {busy ? 'Salvando...' : 'Cadastrar conta'}</button></form><div className="panel list-panel"><div className="panel-heading"><div><h3>Minhas contas</h3><p>{contas.length} conta(s) cadastrada(s)</p></div></div>{contas.length ? contas.map((c) => <div className="account-item" key={c.id}><div className="account-icon"><Icon name="wallet" /></div><div className="account-info"><strong>{c.nome}</strong><span>{c.tipoConta}</span></div><b>{money(c.saldoInicial)}</b></div>) : <Empty text="Nenhuma conta cadastrada." />}</div></div></div>;
}

function CategoriesPage({ categorias, busy, runAction }) {
  const [form, setForm] = useState({ nome: '', tipo: 'DESPESA' });
  const submit = (e) => { e.preventDefault(); runAction(() => api.criarCategoria(form), 'Categoria cadastrada com sucesso!'); setForm({ nome: '', tipo: 'DESPESA' }); };
  return <div className="page-content"><PageHeader title="Categorias" description="Classifique suas receitas e despesas para entender para onde vai seu dinheiro." /><div className="split-layout"><form className="panel form-panel" onSubmit={submit}><h3>Nova categoria</h3><p className="form-intro">Escolha um nome e classifique o tipo.</p><Field label="Nome da categoria"><input required maxLength="80" placeholder="Ex.: Alimentação" value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} /></Field><Field label="Tipo"><select value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })}><option value="DESPESA">Despesa</option><option value="RECEITA">Receita</option></select></Field><button className="primary-button full-button" disabled={busy}><Icon name="plus" /> Cadastrar categoria</button></form><div className="panel list-panel"><div className="panel-heading"><div><h3>Categorias cadastradas</h3><p>{categorias.length} categoria(s)</p></div></div>{categorias.length ? categorias.map((c) => <div className="category-item" key={c.id}><div className={`category-symbol ${c.tipo === 'RECEITA' ? 'category-income' : 'category-expense'}`}><Icon name="tag" /></div><div className="account-info"><strong>{c.nome}</strong><span>{c.tipo === 'RECEITA' ? 'Receita' : 'Despesa'}</span></div><span className={`type-pill ${c.tipo === 'RECEITA' ? 'pill-income' : 'pill-expense'}`}>{c.tipo}</span></div>) : <Empty text="Nenhuma categoria cadastrada." />}</div></div></div>;
}

function TransactionsPage({ data, busy, runAction }) {
  const [form, setForm] = useState({ descricao: '', valor: '', dataLancamento: localToday(), tipo: 'DESPESA', contaId: '', categoriaId: '' });
  const filteredCategories = data.categorias.filter((c) => c.tipo === form.tipo);
  const submit = (e) => {
    e.preventDefault();
    runAction(() => api.criarLancamento({ ...form, valor: Number(form.valor), contaId: Number(form.contaId), categoriaId: Number(form.categoriaId) }), 'Lançamento registrado com sucesso!');
    setForm({ ...form, descricao: '', valor: '' });
  };
  const remove = (id) => { if (window.confirm('Deseja realmente excluir este lançamento?')) runAction(() => api.excluirLancamento(id), 'Lançamento excluído.'); };
  return <div className="page-content"><PageHeader title="Lançamentos" description="Registre e acompanhe tudo o que entra e sai das suas contas." /><form className="panel transaction-form" onSubmit={submit}><div className="panel-heading"><div><h3>Novo lançamento</h3><p>Preencha os dados da movimentação.</p></div></div><div className="form-grid"><Field label="Descrição"><input required maxLength="150" placeholder="Ex.: Compra no mercado" value={form.descricao} onChange={(e) => setForm({ ...form, descricao: e.target.value })} /></Field><Field label="Valor (R$)"><input required type="number" min="0.01" step="0.01" placeholder="0,00" value={form.valor} onChange={(e) => setForm({ ...form, valor: e.target.value })} /></Field><Field label="Data"><input required type="date" value={form.dataLancamento} onChange={(e) => setForm({ ...form, dataLancamento: e.target.value })} /></Field><Field label="Tipo"><select value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value, categoriaId: '' })}><option value="DESPESA">Despesa</option><option value="RECEITA">Receita</option></select></Field><Field label="Conta"><select required value={form.contaId} onChange={(e) => setForm({ ...form, contaId: e.target.value })}><option value="">Selecione uma conta</option>{data.contas.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}</select></Field><Field label="Categoria"><select required value={form.categoriaId} onChange={(e) => setForm({ ...form, categoriaId: e.target.value })}><option value="">Selecione uma categoria</option>{filteredCategories.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}</select></Field></div><button className="primary-button" disabled={busy || !data.contas.length || !filteredCategories.length}><Icon name="plus" /> Registrar lançamento</button>{!data.contas.length && <p className="inline-warning">Cadastre uma conta antes de registrar movimentações.</p>}</form><section className="panel transactions-panel"><div className="panel-heading"><div><h3>Todas as movimentações</h3><p>{data.lancamentos.length} registro(s)</p></div></div><TransactionTable items={[...data.lancamentos].sort((a, b) => String(b.dataLancamento).localeCompare(String(a.dataLancamento)) || Number(b.id) - Number(a.id))} onDelete={remove} /></section></div>;
}

function BudgetsPage({ data, rows, busy, runAction }) {
  const current = new Date();
  const [form, setForm] = useState({ mes: String(current.getMonth() + 1), ano: String(current.getFullYear()), limite: '', categoriaId: '' });
  const expenseCategories = data.categorias.filter((c) => c.tipo === 'DESPESA');
  const submit = (e) => { e.preventDefault(); runAction(() => api.criarOrcamento({ mes: Number(form.mes), ano: Number(form.ano), limite: Number(form.limite), categoriaId: Number(form.categoriaId) }), 'Orçamento cadastrado com sucesso!'); setForm({ ...form, limite: '' }); };
  const remove = (id) => { if (window.confirm('Deseja excluir este orçamento?')) runAction(() => api.excluirOrcamento(id), 'Orçamento excluído.'); };
  return <div className="page-content"><PageHeader title="Orçamentos" description="Defina limites por categoria e acompanhe o quanto já foi utilizado." /><form className="panel transaction-form" onSubmit={submit}><div className="panel-heading"><div><h3>Novo orçamento</h3><p>O limite é individual por categoria e período.</p></div></div><div className="form-grid budget-form-grid"><Field label="Mês"><select value={form.mes} onChange={(e) => setForm({ ...form, mes: e.target.value })}>{Array.from({ length: 12 }, (_, i) => <option key={i + 1} value={i + 1}>{new Date(2026, i, 1).toLocaleDateString('pt-BR', { month: 'long' })}</option>)}</select></Field><Field label="Ano"><input required type="number" min="2000" value={form.ano} onChange={(e) => setForm({ ...form, ano: e.target.value })} /></Field><Field label="Limite (R$)"><input required type="number" min="0.01" step="0.01" placeholder="0,00" value={form.limite} onChange={(e) => setForm({ ...form, limite: e.target.value })} /></Field><Field label="Categoria de despesa"><select required value={form.categoriaId} onChange={(e) => setForm({ ...form, categoriaId: e.target.value })}><option value="">Selecione</option>{expenseCategories.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}</select></Field></div><button className="primary-button" disabled={busy || !expenseCategories.length}><Icon name="plus" /> Criar orçamento</button></form><div className="budget-card-grid">{data.orcamentos.map((budget) => { const spent = data.lancamentos.filter((x) => x.tipo === 'DESPESA' && Number(x.categoriaId) === Number(budget.categoriaId) && new Date(`${x.dataLancamento}T12:00:00`).getMonth() + 1 === Number(budget.mes) && new Date(`${x.dataLancamento}T12:00:00`).getFullYear() === Number(budget.ano)).reduce((sum, x) => sum + Number(x.valor), 0); const percent = Number(budget.limite) ? spent / Number(budget.limite) * 100 : 0; return <div className="panel budget-detail" key={budget.id}><div className="budget-detail-head"><div><span className="eyebrow">ORÇAMENTO</span><h3>{budget.categoriaNome}</h3></div><button className="delete-button" onClick={() => remove(budget.id)} title="Excluir orçamento"><Icon name="trash" /></button></div><span className="budget-period">{monthLabel(budget.mes, budget.ano)}</span><div className="budget-large-value">{money(spent)} <small>/ {money(budget.limite)}</small></div><div className="bar-track"><div className={`bar-fill ${percent > 100 ? 'red-fill' : 'blue-fill'}`} style={{ width: `${Math.min(percent, 100)}%` }} /></div><div className="budget-detail-foot"><span>Utilizado</span><strong className={percent > 100 ? 'text-danger' : ''}>{Math.round(percent)}%</strong></div></div>; })}</div>{!data.orcamentos.length && <div className="panel"><Empty text="Você ainda não cadastrou nenhum orçamento." /></div>}</div>;
}

function Field({ label, children }) { return <label className="field"><span>{label}</span>{children}</label>; }

export default App;
