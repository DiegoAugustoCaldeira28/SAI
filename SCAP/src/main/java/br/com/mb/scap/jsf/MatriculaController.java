package br.com.mb.scap.jsf;

import br.com.mb.scap.jsf.util.ConnectionEmail;
import br.com.mb.scap.jpa.entities.Aluno;
import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.ChamadaAula;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Instrutor;
import br.com.mb.scap.jpa.entities.Matricula;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jpa.session.ChamadaAulaFacade;
import br.com.mb.scap.jpa.session.ChamadaFacade;
import br.com.mb.scap.jpa.session.DataEventoFacade;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.MatriculaFacade;
import br.com.mb.scap.jsf.util.ConexaoReport;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;

@ManagedBean(name = "matriculaController")
@SessionScoped
public class MatriculaController implements Serializable {

    private Matricula current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.MatriculaFacade ejbFacade;
    @EJB
    private DataEventoFacade dataEventoFacade;
    @EJB
    private ChamadaFacade chamadaFacade;
    @EJB
    private ChamadaAulaFacade chamadaAulaFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Matricula> matricula;
    private List<Matricula> pago;
    private List<Matricula> quitados;
    private List<Matricula> espera;
    private int vagasRestante;
    private Instrutor currentIns;
    private br.com.mb.scap.jpa.session.InstrutorFacade ejbFacadeIns;
    private List<Aluno> alunosOrdenado;

    public MatriculaController() {
    }

    public Matricula getSelected() {
        if (current == null) {
            current = new Matricula();
            setPago(new ArrayList<Matricula>());
            setMatricula(new ArrayList<Matricula>());
            setQuitados(new ArrayList<Matricula>());
            selectedItemIndex = -1;
        }
        return current;
    }

    private MatriculaFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(1000) {

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

    public String prepareCreate() {
        current = new Matricula();
        selectedItemIndex = -1;
        recreateModel();
        return "Create_1";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("MatriculaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("MatriculaUpdated"));
            return "Create_1";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Matricula) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "Create_1";
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("MatriculaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
        }
    }

    public void carregaMatricula(ValueChangeEvent event) {
        current.setIdaluno((Aluno) event.getNewValue());
        setMatricula(ejbFacade.findByAlunoNPago(current.getIdaluno()));
        quitados = ejbFacade.findByAlunoPago(current.getIdaluno());
        List<Matricula> realoficial = new ArrayList<>();
        for (Matricula item : getMatricula()) {
            System.out.println("Chegou Controller");
            int pos = ejbFacade.countPosicao(item).intValue();
            if (!(pos >= item.getIdevento().getVagasTotais())) {
                realoficial.add(item);
            }
        }
        setMatricula(realoficial);
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
        return "Create_1";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "Create_1";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Matricula getMatricula(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Matricula.class)
    public static class MatriculaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MatriculaController controller = (MatriculaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "matriculaController");
            return controller.getMatricula(getKey(value));
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
            if (object instanceof Matricula) {
                Matricula o = (Matricula) object;
                return getStringKey(o.getIdmatricula());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Matricula.class.getName());
            }
        }

    }

    /**
     * @return the alunosOrdenado
     */
    public List<Aluno> getAlunosOrdenado() {
        alunosOrdenado = ejbFacade.listAllOrdenado();
        return alunosOrdenado;
    }

    /**
     * @param alunosOrdenado the alunosOrdenado to set
     */
    public void setAlunosOrdenado(List<Aluno> alunosOrdenado) {
        this.alunosOrdenado = alunosOrdenado;
    }

    /**
     * @param id
     * @return the matricula
     */
    public Matricula getMatriculaOne(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    public List<Matricula> getMatricula() {
        return matricula;
    }

    /**
     * @param matricula the matricula to set
     */
    public void setMatricula(List<Matricula> matricula) {
        this.matricula = matricula;
    }

    /**
     * @return the pago
     */
    public List<Matricula> getPago() {
        return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(List<Matricula> pago) {
        this.pago = pago;
    }

    /**
     * @return the quitados
     */
    public List<Matricula> getQuitados() {
        return quitados;
    }

    /**
     * @param quitados the quitados to set
     */
    public void setQuitados(List<Matricula> quitados) {
        this.quitados = quitados;
    }

    public Instrutor getInstrutor(java.lang.Integer id) {
        return ejbFacadeIns.find(id);
    }

    public Instrutor getInstrutor() {
        if (currentIns == null) {
            currentIns = new Instrutor();
        }
        return currentIns;
    }

    public String pagando() {
        String email = null;
        ArrayList<Chamada> ch = new ArrayList<>();
        ArrayList<ChamadaAula> chA = new ArrayList<>();
        ArrayList<DataEvento> dataEventos;
        System.out.println(pago);
        Usuario usuario = SessionContext.getInstance().getUsuarioLogado();
        for (Matricula item : pago) {
            item.setPago(Boolean.TRUE);
            item.setIdusuario(usuario);
            ejbFacade.edit(item);
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

        matricula = new ArrayList<>();
        pago = new ArrayList<>();
        quitados = new ArrayList<>();
        imprimeComprovante();
        JsfUtil.addSuccessMessage("Matricula atualizada com sucesso");
        //ConnectionEmail.sendEmail(current.getIdaluno().getEmail(), origem);
        return prepareCreate();
    }

    public void imprimeComprovante() {
        ConexaoReport conexao = new ConexaoReport();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("id", current.getIdaluno().getIdaluno());
        ConnectionEmail.sendEmail(current.getIdaluno().getEmail(), conexao.geraRelatorio("ComprovantePagamento_A4.jasper", "Pagamento", parametros));
    }

    public int getQuantidadeSelecionados() {
        return this.pago.size();
    }

    public String getValorTotalSelecionados() {
        double total = 0;
        for (Matricula m : pago) {
            total += m.getIdevento().getCusto();
        }
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance();
        return formatoMoeda.format(total);
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
                Logger.getLogger(MatriculaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }

}
