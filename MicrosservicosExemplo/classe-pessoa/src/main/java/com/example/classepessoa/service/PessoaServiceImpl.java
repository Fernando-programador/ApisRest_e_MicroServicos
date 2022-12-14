package com.example.classepessoa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.classepessoa.compartilhado.AnimalDto;
import com.example.classepessoa.compartilhado.PessoaDto;
import com.example.classepessoa.http.AnimaisFeignClients;
import com.example.classepessoa.model.Pessoa;
import com.example.classepessoa.repository.PessoaRepository;



@Service
public class PessoaServiceImpl implements PessoaService {
    @Autowired
    private PessoaRepository repo;

    @Autowired
    private AnimaisFeignClients animaisFeign;

    @Override
    public PessoaDto criarPessoa(PessoaDto pessoa) {
        return salvarPessoa(pessoa);
    }

    @Override
    public List<PessoaDto> obterTodos() {
        List<Pessoa> pessoas = repo.findAll();

        return pessoas.stream()
            .map(pessoa -> new ModelMapper().map(pessoa, PessoaDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<PessoaDto> obterPorId(Integer id) {
       Optional<Pessoa> pessoa = repo.findById(id);

       if(pessoa.isPresent()) {

            PessoaDto dto = new ModelMapper().map(pessoa.get(), PessoaDto.class);
            
            //trazer los animais daa pessoas pelo id
            List<AnimalDto> animais = animaisFeign.obterAnimais(id);
            
            //adiciono a lista de animais na pessoa dto
            dto.setAnimais(animais);

            return Optional.of(dto);
       }

       return Optional.empty();
    }

    @Override
    public void removerPessoa(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public PessoaDto atualizarPessoa(Integer id, PessoaDto pessoa) {
        pessoa.setId(id);
        return salvarPessoa(pessoa);
    }

    private PessoaDto salvarPessoa(PessoaDto pessoa) {
        ModelMapper mapper = new ModelMapper();
        Pessoa pessoaEntidade = mapper.map(pessoa, Pessoa.class);
        pessoaEntidade = repo.save(pessoaEntidade);

        return mapper.map(pessoaEntidade, PessoaDto.class);
    }
}
