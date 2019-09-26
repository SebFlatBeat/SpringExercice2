package com.seb.web;

        import com.seb.entities.Compte;
        import com.seb.entities.Operation;
        import com.seb.service.IBanqueService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.data.domain.Page;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BanqueController {
    @Autowired
    private IBanqueService banqueService;

    @RequestMapping("/operations")
    public String index(){
        return "comptes";
    }

    @RequestMapping("/consulterCompte")
    public String consulter(Model model, String codeCompte,
                            @RequestParam(name = "page",defaultValue = "0") int page,@RequestParam(name = "size", defaultValue = "5") int size){
        model.addAttribute("codeCompte",codeCompte);
        try{
            Compte cp = banqueService.consulterCompte(codeCompte);
            Page<Operation> operationPage = banqueService.listOperation(codeCompte,page,size);
            model.addAttribute("listOperation", operationPage.getContent());
            int[] pages = new int[operationPage.getTotalPages()];
            model.addAttribute("pages",pages);
            model.addAttribute("compte",cp);
        }catch (Exception e){
            model.addAttribute("exception",e);
        }
        return "comptes";
    }

    @RequestMapping(value = "/saveOperation", method = RequestMethod.POST)
    public String saveOperation(Model model,String typeOperation,String codeCompte, double montant, String codeCompte2){
        try{

    if(typeOperation.equals("VERS")){
        banqueService.verser(codeCompte,montant);
    }else if(typeOperation.equals("RETR")){
        banqueService.retirer(codeCompte,montant);
    }if (typeOperation.equals("VIR")){
        banqueService.virement(codeCompte,codeCompte2,montant);
        }
        }catch (Exception e){
            model.addAttribute("error", e);
            return "redirect:/consulterCompte?codeCompte="+codeCompte+"&error="+e.getMessage();
        }
        return "redirect:/consulterCompte?codeCompte="+codeCompte;
    }

}
