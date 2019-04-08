package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.UsuarioFacade;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    private Usuario current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.UsuarioFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    //Adicionais
    private ArrayList<Usuario> usuariosFiltrados;

    public UsuarioController() {
    }

    public Usuario getSelected() {
        if (current == null) {
            current = new Usuario();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UsuarioFacade getFacade() {
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
        current = new Usuario();
        selectedItemIndex = -1;
        recreateModel();
        return "Create";
    }

    public String create() {
        try {
            current.setSenha("2018sai");
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("UsuarioCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String createSenha() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage("Senha Alterada com Sucesso");
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("UsuarioUpdated"));
            cancelSelected();
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy(Usuario usuario) {
        current = usuario;
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("UsuarioDeleted"));
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

    public Usuario getUsuario(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the usuariosFiltrados
     */
    public ArrayList<Usuario> getUsuariosFiltrados() {
        return usuariosFiltrados;
    }

    /**
     * @param usuariosFiltrados the usuariosFiltrados to set
     */
    public void setUsuariosFiltrados(ArrayList<Usuario> usuariosFiltrados) {
        this.usuariosFiltrados = usuariosFiltrados;
    }

    public void editar(Usuario usuario) {
        this.current = usuario;
    }

    public void cancelSelected() {
        this.current = null;
    }

    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
            return controller.getUsuario(getKey(value));
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
            if (object instanceof Usuario) {
                Usuario o = (Usuario) object;
                return getStringKey(o.getIdusuario());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Usuario.class.getName());
            }
        }

    }

    public String autenticar() throws Exception {
        Usuario i;
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            i = ejbFacade.findByCPF(current.getCpf());
            if ((i.getCpf() == null ? current.getCpf() != null : !i.getCpf().equals(current.getCpf()))
                    || (i.getSenha() == null ? current.getSenha() != null : !i.getSenha().equals(current.getSenha()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario ou Senha incorretos"));
                return "Login.xhtml?faces-redirect=true";

            }

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario ou Senha incorretos"));
            return "Login.xhtml?faces-redirect=true";
        }

        System.out.println(" Usuario: " + i);

        SessionContext.getInstance().setAttribute("usuarioLogado", i);
        return "/matricula/Create_1.xhtml";

    }

    public String logout() {
        System.out.println(SessionContext.getInstance().getUsuarioLogado());
        System.out.println("Sessao:" + SessionContext.getInstance());

        SessionContext.getInstance().encerrarSessao();
        current = null;
        return "/usuario/Login.xhtml?faces-redirect=true";

    }

    public String editarSenha() {
        current = SessionContext.getInstance().getUsuarioLogado();
        return "/usuario/Editar.xhtml";
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
                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }
}
