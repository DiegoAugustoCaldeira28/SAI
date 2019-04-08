package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Instrutor;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jpa.session.DataEventoFacade;
import br.com.mb.scap.jpa.session.EventoFacade;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.InstrutorFacade;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import static javax.swing.text.html.CSS.getAttribute;

@SessionScoped
@Named("instrutorController")
public class InstrutorController implements Serializable {

    private Instrutor current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.InstrutorFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Evento> eventos = new ArrayList<>();
    @EJB
    private EventoFacade eventoFacade;
    @EJB
    private DataEventoFacade dataEventoFacade;

    //Adicionais
    private ArrayList<Instrutor> instrutoresFiltrado;
    private List<Chamada> chamada = new ArrayList<>();
    

    public InstrutorController() {
    }

    @PostConstruct
    public void init() {
        eventos = (List<Evento>) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("lista");
    }

    public Instrutor getSelected() {
        if (current == null) {
            current = new Instrutor();
            selectedItemIndex = -1;
        }
        return current;
    }

    private InstrutorFacade getFacade() {
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
        current = new Instrutor();
        selectedItemIndex = -1;
        recreateModel();
        return "Create_1";
    }

    public String create() {
        try {
            current.setAdministrador(Boolean.FALSE);
            current.setSenha("2018sai");
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("InstrutorCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("InstrutorUpdated"));
            cancelSelected();
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy(Instrutor instrutor) {
        current = instrutor;
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        cancelSelected();
        return "List";
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("InstrutorDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
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

    public Instrutor getInstrutor(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the instrutoresFiltrado
     */
    public ArrayList<Instrutor> getInstrutoresFiltrado() {
        return instrutoresFiltrado;
    }

    /**
     * @param instrutoresFiltrado the instrutoresFiltrado to set
     */
    public void setInstrutoresFiltrado(ArrayList<Instrutor> instrutoresFiltrado) {
        this.instrutoresFiltrado = instrutoresFiltrado;
    }

    public void editar(Instrutor instrutor) {
        this.current = instrutor;
    }

    public void cancelSelected() {
        this.current = null;
    }

    /**
     * @return the eventosInstrutor
     */
    public List<Evento> getEventos() {
        return eventos;
    }

    /**
     * @param eventosInstrutor the eventosInstrutor to set
     */
    public void setEventos(List<Evento> eventosInstrutor) {
        this.eventos = eventosInstrutor;
    }

    @FacesConverter(forClass = Instrutor.class)
    public static class InstrutorControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InstrutorController controller = (InstrutorController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "instrutorController");
            return controller.getInstrutor(getKey(value));
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
            if (object instanceof Instrutor) {
                Instrutor o = (Instrutor) object;
                return getStringKey(o.getIdinstrutor());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Instrutor.class.getName());
            }
        }

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
                Logger.getLogger(InstrutorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }

    public String autenticar() throws Exception {
        Instrutor i;
        System.out.println("ta chegando");
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            i = ejbFacade.findByCPF(current.getCpf());
            if ((i.getCpf() == null ? current.getCpf() != null : !i.getCpf().equals(current.getCpf()))
                    || (i.getSenha() == null ? current.getSenha() != null : !i.getSenha().equals(current.getSenha()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario ou Senha incorretos"));
                return "LoginChamada.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario ou Senha incorretos"));
            return "LoginChamada.xhtml?faces-redirect=true";
        }

        System.out.println("Instrutor: " + i);

        SessionContext.getInstance().setAttribute("instrutorLogado", i);
        if (i.getTipo()) {
            return "/chamadaAula/ControleChamada.xhtml";
        } else {            
            return "/chamada/Create_1.xhtml";
        }
    }

    public String autenticarRelatorio() throws Exception {
        Instrutor i;
        System.out.println("ta chegando");
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            i = ejbFacade.findByCPF(current.getCpf());
            if ((i.getCpf() == null ? current.getCpf() != null : !i.getCpf().equals(current.getCpf()))
                    || (i.getSenha() == null ? current.getSenha() != null : !i.getSenha().equals(current.getSenha()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario ou Senha incorretos"));
                return "LoginRelatorio.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario ou Senha incorretos"));
            return "LoginRelatorio.xhtml?faces-redirect=true";
        }

        System.out.println("Instrutor: " + i);

        SessionContext.getInstance().setAttribute("instrutorLogado", i);
        eventos = eventoFacade.findbyAll();
        context.getExternalContext().getFlash().put("lista", eventos);
        if (i.getAdministrador()) {
            return "/chamada/Relatorio.xhtml";
        } else {
            return "LoginRelatorio.xhtml?faces-redirect=true";
        }
    }
    
    public String logout() {
        System.out.println(SessionContext.getInstance().getInstrutorLogado());
        System.out.println("Sessao:" + SessionContext.getInstance());

        SessionContext.getInstance().encerrarSessao();
        current = null;
        return "/instrutor/LoginChamada.xhtml?faces-redirect=true";
    }

    public String logoutRelatorio() {
        System.out.println(SessionContext.getInstance().getInstrutorLogado());
        System.out.println("Sessao:" + SessionContext.getInstance());

        SessionContext.getInstance().encerrarSessao();
        current = null;
        return "/instrutor/LoginRelatorio.xhtml?faces-redirect=true";

    }
            
    public String editarSenha() {
        current = SessionContext.getInstance().getInstrutorLogado();
        return "/usuario/Editar.xhtml";
    }
    
    public boolean getInstrutorLogado(){
        getSelected();
        Instrutor instrutor = SessionContext.getInstance().getInstrutorLogado();
        ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        if (instrutor == null) {
            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/faces/instrutor/LoginRelatorio.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instrutor != null;
    }

    public void carregaChamada(ValueChangeEvent event) {
        Evento e = (Evento) event.getNewValue();
        setChamada(dataEventoFacade.carregaChamada(dataEventoFacade.uniqueDataEvento(e.getIdevento())));
    }

    /**
     * @return the chamada
     */
    public List<Chamada> getChamada() {
        return chamada;
    }

    /**
     * @param chamada the chamada to set
     */
    public void setChamada(List<Chamada> chamada) {
        this.chamada = chamada;
    }
    
}
