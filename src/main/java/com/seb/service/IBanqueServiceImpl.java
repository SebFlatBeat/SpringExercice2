package com.seb.service;

import com.seb.dao.CompteRepository;
import com.seb.dao.OperationRepository;
import com.seb.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class IBanqueServiceImpl implements IBanqueService {
    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Override
    public Compte consulterCompte(String codeCpte) {
       Optional <Compte> compte = compteRepository.findById(codeCpte);
        Compte cp = null;
        if(compte.isPresent()) {
            cp = compte.get();
        }else throw new RuntimeException("Compte Introuvable");
        return cp;
    }

    @Override
    public void verser(String codeCpte, double montant) {
        Compte cp = consulterCompte(codeCpte);
        Versement v = new Versement(new Date(),montant,cp);
        operationRepository.save(v);
        cp.setSolde(cp.getSolde()+montant);
        compteRepository.save(cp);
    }

    @Override
    public void retirer(String codeCpte, double montant) {
        Compte cp = consulterCompte(codeCpte);
        double facilitesCaisse = 0;
        if(cp instanceof CompteCourant)
            facilitesCaisse=((CompteCourant) cp).getDecouvert();
        if(cp.getSolde()+facilitesCaisse<montant)
            throw new RuntimeException("Solde insuffisant");
        Retrait r = new Retrait(new Date(),montant,cp);
        operationRepository.save(r);
        cp.setSolde(cp.getSolde()-montant);
        compteRepository.save(cp);
    }

    @Override
    public void virement(String codeCpte1, String codeCpte2, double montant) {
        if(codeCpte1.equals(codeCpte2))
            throw new RuntimeException("Impossible de faire un virement sur le même compte");
        retirer(codeCpte1,montant);
        verser(codeCpte2,montant);
    }

    @Override
    public Page<Operation> listOperation(String codeCpte, int page, int size) {

        return operationRepository.listOperation(codeCpte, new PageRequest(page,size));
    }
}
