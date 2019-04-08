package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.EventoFacade;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean(name = "eventoController")
@SessionScoped
public class EventoController implements Serializable {

    private Evento current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.EventoFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    //Adicional
    private ArrayList<Evento> eventosFiltrados;

    public EventoController() {
    }

    public Evento getSelected() {
        if (current == null) {
            current = new Evento();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EventoFacade getFacade() {
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
        current = new Evento();
        selectedItemIndex = -1;
        recreateModel();
        return "Create_1";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("EventoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("EventoUpdated"));
            cancelSelected();
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy(Evento evento) {
        current = evento;
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("EventoDeleted"));
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

    public Evento getEvento(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the eventosFiltrados
     */
    public ArrayList<Evento> getEventosFiltrados() {
        return eventosFiltrados;
    }

    /**
     * @param eventosFiltrados the eventosFiltrados to set
     */
    public void setEventosFiltrados(ArrayList<Evento> eventosFiltrados) {
        this.eventosFiltrados = eventosFiltrados;
    }

    public void editar(Evento evento) {
        this.current = evento;
    }

    public void cancelSelected() {
        this.current = null;
    }

    @FacesConverter(forClass = Evento.class)
    public static class EventoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EventoController controller = (EventoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "eventoController");
            return controller.getEvento(getKey(value));
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
            if (object instanceof Evento) {
                Evento o = (Evento) object;
                return getStringKey(o.getIdevento());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Evento.class.getName());
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
                Logger.getLogger(EventoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }

}
