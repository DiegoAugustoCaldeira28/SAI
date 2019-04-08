/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jsf.util;

import br.com.mb.scap.jpa.entities.Instrutor;
import br.com.mb.scap.jpa.entities.Usuario;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
  
  
public class SessionContext {
     
    private static SessionContext instance;
     
    public static SessionContext getInstance(){
         if (instance == null){
             instance = new SessionContext();
         }
          
         return instance;
    }
     
    private SessionContext(){
          
    }
     
    private ExternalContext currentExternalContext(){
         if (FacesContext.getCurrentInstance() == null){
             throw new RuntimeException("O FacesContext não pode ser chamado fora de uma requisição HTTP");
         }else{
             return FacesContext.getCurrentInstance().getExternalContext();
         }
    }
     
    public Usuario getUsuarioLogado(){
         return (Usuario) getAttribute("usuarioLogado");
    }
     
    public void setUsuarioLogado(Usuario usuario){
         setAttribute("usuarioLogado", usuario);
    }
    
    public Instrutor getInstrutorLogado(){
         return (Instrutor) getAttribute("instrutorLogado");
    }
     
    public void setUsuarioLogado(Instrutor instrutor){
         setAttribute("instrutorLogado", instrutor);
    }
     
    public void encerrarSessao(){   
         currentExternalContext().invalidateSession();
    }
     
    public Object getAttribute(String nome){
         return currentExternalContext().getSessionMap().get(nome);
    }
     
    public void setAttribute(String nome, Object valor){
         currentExternalContext().getSessionMap().put(nome, valor);
    }
     
}
