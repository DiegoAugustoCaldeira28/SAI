package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Aluno;
import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.ChamadaAula;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Matricula;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.AlunoFacade;
import br.com.mb.scap.jpa.session.ChamadaAulaFacade;
import br.com.mb.scap.jpa.session.ChamadaFacade;
import br.com.mb.scap.jpa.session.DataEventoFacade;
import br.com.mb.scap.jpa.session.MatriculaFacade;
import br.com.mb.scap.jsf.util.ConexaoReport;
import br.com.mb.scap.jsf.util.ConnectionEmail;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean(name = "alunoController")
@SessionScoped
public class AlunoController implements Serializable {

    @EJB
    private ChamadaAulaFacade chamadaAulaFacade;

    @EJB
    private ChamadaFacade chamadaFacade;

    @EJB
    private MatriculaFacade matriculaFacade;

    @EJB
    private DataEventoFacade dataEventoFacade;

    private Aluno current;
    private DataModel items = null;
    @EJB
    private AlunoFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Aluno> alunosOrdenado;

    //Atributos adicionais
    private List<DataEvento> eventosSelecionados;
    private ArrayList<DataEvento> todosDatasEventos;
    private ArrayList<DataEvento> eventosFiltrados;

    public AlunoController() {
    }

    public Aluno getSelected() {
        if (current == null) {
            current = new Aluno();
            setEventosSelecionados(new ArrayList<DataEvento>());
            current.setExterno(Boolean.TRUE);
            selectedItemIndex = -1;
        }
        return current;
    }

    private AlunoFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Aluno) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Aluno();
        current.setExterno(Boolean.TRUE);
        selectedItemIndex = -1;
        eventosSelecionados = new ArrayList<>();
        todosDatasEventos = new ArrayList<>();
        recreateModel();
        return "Sair";
    }

    public void imprimeComprovante(int id, String email) {
        ConexaoReport conexao = new ConexaoReport();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("id", id);
        ConnectionEmail.sendEmail(email, conexao.geraRelatorio("ComprovanteInscricao_A4.jasper", "Inscricao", parametros));
    }

    public String prepareEdit() {
        current = (Aluno) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("AlunoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Aluno) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("AlunoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Aluno getAluno(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    //Funcoes Adicionais
    public String escolheDepartamento(String curso) {
        String departamento = "";
        if (curso.equalsIgnoreCase("BACHARELADO EM CIENCIA DA COMPUTACAO")
                || curso.equalsIgnoreCase("TECNOLOGIA EM ANALISE DESEN. DE SISTEMAS")) {
            departamento = "DEPARTAMENTO DE INFORMATICA";
        } else if ((curso.equalsIgnoreCase("TECNOLOGIA EM ALIMENTOS"))
                || (curso.equalsIgnoreCase("ENGENHARIA DE BIOPROCESSOS E BIOTECNOLOGIA"))) {
            departamento = "DEPARTAMENTO DE ALIMENTOS";
        } else if (curso.equalsIgnoreCase("ENGENHARIA DE PRODUCAO")) {
            departamento = "DEPARTAMENTO DE ENGENHARIA DE PRODUCAO";
        } else if (curso.equalsIgnoreCase("ENGENHARIA MECANICA")
                || (curso.equalsIgnoreCase("TECNOLOGIA EM FABRICACAO MECANICA"))) {
            departamento = "DEPARTAMENTO DE ENGENHARIA MECANICA";
        } else if (curso.equalsIgnoreCase("ENGENHARIA QUIMICA")) {
            departamento = "DEPARTAMENTO DE ENGENHARIA QUIMICA";
        } else if (curso.equalsIgnoreCase("LICENCIATURA EM CIENCIAS BIOLOGICAS")
                || curso.equalsIgnoreCase("LICENCIATURA EM CIENCIAS NATURAIS")) {
            departamento = "DEPARTAMENTO DE ENSINO";
        } else if ((curso.equalsIgnoreCase("ENGENHARIA ELETRONICA"))
                || (curso.equalsIgnoreCase("TECNOLOGIA EM AUTOMACAO INDUSTRIAL"))) {
            departamento = "DEPARTAMENTO DE ENGENHARIA ELETRONICA/ELETRICA";
        }

        return departamento;
    }

    public void carregaCursos(ValueChangeEvent event) {
        String cpf = (String) event.getNewValue();
        System.out.println(cpf);
        current.setCpf(cpf);
        current = ejbFacade.findCPF(current.getCpf());
        String curso = current.getCurso();//(String) event.getNewValue();
        eventosSelecionados = new ArrayList<>();
        getTodosDatasEventos(escolheDepartamento(curso));
        System.out.println("Eventos:" + todosDatasEventos + "Curso:" + escolheDepartamento(curso));
    }

    public void carregaCursosCadastro(ValueChangeEvent event) {
        String curso = (String) event.getNewValue();
        eventosSelecionados = new ArrayList<>();
        getTodosDatasEventos(escolheDepartamento(curso));
        System.out.println("Eventos:" + todosDatasEventos + "Curso:" + escolheDepartamento(curso));
    }

    public ArrayList<DataEvento> getTodosDatasEventos(String curso) {
        this.todosDatasEventos = new ArrayList<>(dataEventoFacade.findCurso(curso));

        for (DataEvento item : todosDatasEventos) {
            int ocupadas = dataEventoFacade.vagasFechadas(item.getIdevento());
            if ((item.getIdevento().getVagasTotais() - ocupadas) == 0) {
                item.getIdevento().setVagasTotais(0);
            } else {
                item.getIdevento().setVagasTotais(item.getIdevento().getVagasTotais() - ocupadas);
            }
        }
        return this.todosDatasEventos;
    }

    public ArrayList<DataEvento> getTodosDatasEventos() {
        return this.todosDatasEventos;

    }

    /**
     * @return the eventosSelecionados
     */
    public List<DataEvento> getEventosSelecionados() {
        return eventosSelecionados;
    }

    /**
     * @param eventosSelecionados the eventosSelecionados to set
     */
    public void setEventosSelecionados(List<DataEvento> eventosSelecionados) {
        this.eventosSelecionados = eventosSelecionados;
    }

    //Funcao Salvar Aluno e Eventos Selecionados
    public String salvar() {
        try {
            List<Matricula> lm = new ArrayList<>(); //Lista a Salvar
            Aluno aluno = ejbFacade.findCPF(current.getCpf()); //Busca por CPF
            List<Matricula> atual = ejbFacade.findAluno(aluno); //List de matricula do Aluno no banco
            if (aluno != null) { //Existe no banco 
                current.setIdaluno(aluno.getIdaluno());
                if (!eventosSelecionados.isEmpty()) {
                    //Comparar se já existe ou se devemos adicionar
                    boolean flag = false;//Flag para ver se o evento e novo ou nao
                    System.out.println(eventosSelecionados);
                    System.out.println(atual);
                    for (DataEvento evento : eventosSelecionados) { //Verificar todos selecioandos
                        for (Matricula matricula : atual) { //Interar com os atuains ja presentes no banco
                            flag = false; //Reinicio da Flag
                            if (evento.getIdevento().getIdevento().equals(
                                    matricula.getIdevento().getIdevento())) {//Verificar se o ID ja existe
                                //Caso já existir no banco
                                //Reidiciona na lista final
                                lm.add(matricula);
                                flag = true; //Tupla Matricula ja existe entao flag alterada
                                break; //Pula para o proximo evento selecionado por ja ter encontrado
                            }
                        }
                        if (!flag) {//Caso seja Evento novo selecionado cria uma tupla para ele e adiciona a lista
                            Matricula m = new Matricula();
                            m.setIdaluno(current);
                            m.setIdevento(evento.getIdevento());
                            m.setPago(Boolean.FALSE);
                            m.setIdusuario(null);
                            lm.add(m);
                        }
                    }
                }
            } else {//Não existe no banco
                if (!eventosSelecionados.isEmpty()) {
                    for (DataEvento item : eventosSelecionados) {
                        Matricula m = new Matricula();
                        m.setIdaluno(current);
                        m.setIdevento(item.getIdevento());
                        m.setPago(Boolean.FALSE);
                        lm.add(m);
                    }
                }
            }
            current.setMatriculaList(lm);
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Cadastro na SAI realizado com sucesso!");
            imprimeComprovante(current.getIdaluno(), current.getEmail());
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String confirmacao() {
        ArrayList<DataEvento> cursosSAI = new ArrayList<>(ejbFacade.findEventoSai());

        if (!eventosSelecionados.containsAll(cursosSAI)) {
            eventosSelecionados.addAll(ejbFacade.findEventoSai()); //Adiciona a mais os Eventos da SAI
        }
        return "Confirmacao";
    }

    public String confirmacaoRecadastro() {
        ArrayList<DataEvento> cursosSAI = new ArrayList<>(ejbFacade.findEventoSai());
        if (!eventosSelecionados.containsAll(cursosSAI)) {
            eventosSelecionados.addAll(ejbFacade.findEventoSai()); //Adiciona a mais os Eventos da SAI
        }
        return "ConfirmacaoRe.xhtml";
    }

    public boolean isEspera(DataEvento dataEvento) {
        System.out.println(dataEvento.getIdevento().getVagasTotais());
        return 0 < dataEvento.getIdevento().getVagasTotais();
    }

    public int getQuantidadeSelecionados() {
        return this.eventosSelecionados.size();
    }

    public String getValorTotalSelecionados() {
        double total = 0;
        for (DataEvento eventosSelecionado : eventosSelecionados) {
            total += eventosSelecionado.getIdevento().getCusto();
        }
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance();
        return formatoMoeda.format(total);
    }

    @FacesConverter(forClass = Aluno.class)
    public static class AlunoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AlunoController controller = (AlunoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "alunoController");
            return controller.getAluno(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Aluno) {
                Aluno o = (Aluno) object;
                return getStringKey(o.getIdaluno());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Aluno.class.getName());
            }
        }

    }

    public boolean verificarAluno() {

        if (current != null && current.getExterno() && (current.getRa() == null || current.getRa().isEmpty())) {
            System.out.println("true");
            return true;
        } else {
            System.out.println("false");
            return false;
        }
    }

    /**
     * @return the eventosFiltrados
     */
    public ArrayList<DataEvento> getEventosFiltrados() {
        return eventosFiltrados;
    }

    /**
     * @param eventosFiltrados the eventosFiltrados to set
     */
    public void setEventosFiltrados(ArrayList<DataEvento> eventosFiltrados) {
        this.eventosFiltrados = eventosFiltrados;
    }

    public void setCampos() {
        Aluno i;
        if (current.getCpf().isEmpty()) {
            current = new Aluno();
        } else {
            i = ejbFacade.findCPF(current.getCpf());

            if (i != null) {
                current = i;
            } else {
                current = null;
            }
        }
    }

    public List<Aluno> getAlunosOrdenado() {
        setAlunosOrdenado(ejbFacade.listAllOrdenado());
        return alunosOrdenado;
    }

    /**
     * @param alunosOrdenado the alunosOrdenado to set
     */
    public void setAlunosOrdenado(List<Aluno> alunosOrdenado) {
        this.alunosOrdenado = alunosOrdenado;
    }

    public String salvando() {
        try {
            List<Matricula> lm = new ArrayList<>(); //Lista a Salvar
            Aluno aluno = ejbFacade.findCPF(current.getCpf()); //Busca por CPF
            List<Matricula> atual = ejbFacade.findAluno(aluno); //List de matricula do Aluno no banco
            if (aluno != null) { //Existe no banco 
                current.setIdaluno(aluno.getIdaluno());
                if (!eventosSelecionados.isEmpty()) {
                    //Comparar se já existe ou se devemos adicionar
                    boolean flag = false;//Flag para ver se o evento e novo ou nao
                    System.out.println(eventosSelecionados);
                    System.out.println(atual);
                    for (DataEvento evento : eventosSelecionados) { //Verificar todos selecioandos
                        for (Matricula matricula : atual) { //Interar com os atuains ja presentes no banco
                            flag = false; //Reinicio da Flag
                            if (evento.getIdevento().getIdevento().equals(
                                    matricula.getIdevento().getIdevento())) {//Verificar se o ID ja existe
                                //Caso já existir no banco
                                //Reidiciona na lista final
                                lm.add(matricula);
                                flag = true; //Tupla Matricula ja existe entao flag alterada
                                break; //Pula para o proximo evento selecionado por ja ter encontrado
                            }
                        }
                        if (!flag) {//Caso seja Evento novo selecionado cria uma tupla para ele e adiciona a lista
                            Matricula m = new Matricula();
                            m.setIdaluno(current);
                            m.setIdevento(evento.getIdevento());
                            m.setPago(Boolean.FALSE);
                            m.setIdusuario(null);
                            lm.add(m);
                        }
                    }
                }
            } else {//Não existe no banco
                if (!eventosSelecionados.isEmpty()) {
                    for (DataEvento item : eventosSelecionados) {
                        Matricula m = new Matricula();
                        m.setIdaluno(current);
                        m.setIdevento(item.getIdevento());
                        m.setPago(Boolean.FALSE);
                        lm.add(m);
                    }
                }
            }
            current.setMatriculaList(lm);
            getFacade().edit(current);
            //JsfUtil.addSuccessMessage("Cadastro na SAI realizado com sucesso!");
            return pagando();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return "Recadastar.xhtml";
        }

    }

    public String pagando() {
        String email = null;
        ArrayList<Chamada> ch = new ArrayList<>();
        ArrayList<ChamadaAula> chA = new ArrayList<>();
        ArrayList<DataEvento> dataEventos;
        ArrayList<Matricula> pago = new ArrayList<>(matriculaFacade.findByAlunoNPago(current));
        System.out.println(pago);
        Usuario usuario = SessionContext.getInstance().getUsuarioLogado();
        for (Matricula item : pago) {
            item.setPago(Boolean.TRUE);
            item.setIdusuario(usuario);
            matriculaFacade.edit(item);
            //System.out.println(item.getIdevento().getTipo());
            if (item.getIdevento().getTipo().equalsIgnoreCase("Palestra")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Workshop")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Maratona de Programacao")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Mesa Redonda")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Visita Tecnica")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Oficina")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Desafio")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Atividade 3 Esferas")) {
                System.out.println("Chegou");
                dataEventos = new ArrayList<>(dataEventoFacade.allDataEvento(item.getIdevento().getIdevento()));
                for (DataEvento dataEvento : dataEventos) {
                    Chamada c = new Chamada();
                    c.setIdaluno(item.getIdaluno());
                    c.setPresente(Boolean.FALSE);
                    c.setIddataEvento(dataEvento);
                    ch.add(c);
                }

            } else if (item.getIdevento().getTipo().equalsIgnoreCase("Minicurso")
                    || item.getIdevento().getTipo().equalsIgnoreCase("Minicurso Computacional")) {
                dataEventos = new ArrayList<>(dataEventoFacade.allDataEvento(item.getIdevento().getIdevento()));
                for (DataEvento dataEvento : dataEventos) {
                    ChamadaAula chamadaAula = new ChamadaAula();
                    chamadaAula.setIdaluno(item.getIdaluno());
                    chamadaAula.setFaltas(0);
                    chamadaAula.setIddataEvento(dataEvento);
                    chA.add(chamadaAula);
                }

            }
            email = item.getIdaluno().getEmail();
        }
        for (Chamada chamada : ch) {
            chamadaFacade.create(chamada);
        }
        for (ChamadaAula chamada : chA) {
            chamadaAulaFacade.create(chamada);
        }

        imprimeComprovante();
        JsfUtil.addSuccessMessage("Inscrição e Matricula atualizada com sucesso");

        return prepareRecadastro();
    }

    public String prepareRecadastro() {
        current = new Aluno();
        current.setExterno(Boolean.TRUE);
        selectedItemIndex = -1;
        eventosSelecionados = new ArrayList<>();
        todosDatasEventos = new ArrayList<>();
        recreateModel();
        return "Recadastrar.xhtml";
    }

    public void imprimeComprovante() {
        ConexaoReport conexao = new ConexaoReport();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("id", current.getIdaluno());
        ConnectionEmail.sendEmail(current.getEmail(), conexao.geraRelatorio("ComprovantePagamento_A4.jasper", "Pagamento", parametros));
    }

    public boolean getUsuarioLogado() {
        getSelected();
        Usuario usuario = SessionContext.getInstance().getUsuarioLogado();
        ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        if (usuario == null) {
            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/faces/usuario/Login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(AlunoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }
}
