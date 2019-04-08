package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Local;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.LocalFacade;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

/**
 *
 * @author matheus
 */
@SessionScoped
@Named("localController")
public class LocalController implements Serializable {

    private Local current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.LocalFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    //Adicionais
    private ArrayList<Local> locaisFiltrados;

    public LocalController() {
    }

    public Local getSelected() {
        if (current == null) {
            current = new Local();
            selectedItemIndex = -1;
        }
        return current;
    }

    private LocalFacade getFacade() {
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
        current = new Local();
        selectedItemIndex = -1;
        recreateModel();
        return "Create_1";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("LocalCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("LocalUpdated"));
            cancelSelected();
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy(Local local) {
        current = local;
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        cancelSelected();
        return "List";
    }

    public void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("LocalDeleted"));
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

    public Local getLocal(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the locaisFiltrados
     */
    public ArrayList<Local> getLocaisFiltrados() {
        return locaisFiltrados;
    }

    /**
     * @param locaisFiltrados the locaisFiltrados to set
     */
    public void setLocaisFiltrados(ArrayList<Local> locaisFiltrados) {
        this.locaisFiltrados = locaisFiltrados;
    }

    public void editar(Local local) {
        this.current = local;
    }

    public void cancelSelected() {
        this.current = null;
    }

    @FacesConverter(forClass = Local.class)
    public static class LocalControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LocalController controller = (LocalController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "localController");
            return controller.getLocal(getKey(value));
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
            if (object instanceof Local) {
                Local o = (Local) object;
                return getStringKey(o.getIdlocal());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Local.class.getName());
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
                Logger.getLogger(LocalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }

}
