/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.spring.basic.controller;


import org.jboss.as.quickstarts.kitchensink.spring.basic.data.MemberDao;
import org.jboss.as.quickstarts.kitchensink.spring.basic.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.asteros.atrium.DB.OrderDB;
import ru.asteros.atrium.DB.RegionInfo;
import ru.asteros.atrium.DB.RegionInfoUtility;
import ru.asteros.atrium.DB.SubOrderDB;
import ru.asteros.atrium.NewBase64;
import ru.asteros.atrium.soap.SOAPHandler;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping(value = "/")
public class MemberController {

    private static Logger log = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberDao memberDao;

    @RequestMapping(value = "start", method = RequestMethod.GET)
    public String test(Model model) {
        OrderDB.addNewOrderWithoutCondition();
        return "redirect:/";
    }

    @RequestMapping(value = "clean", method = RequestMethod.GET)
    public String clear(Model model) {
        OrderDB.clearDB();
        SubOrderDB.clearDB();
        return "redirect:/";
    }
    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public String refresh(Model model) {
        return "redirect:/";
    }

    @RequestMapping(value = "region_edit", method = RequestMethod.GET)
    public String edit(Model model) {
        return "region_edit";
    }

    @RequestMapping(method = RequestMethod.POST,value = "/getfile")
    public @ResponseBody
    void getReviewedFile(HttpServletRequest request, HttpServletResponse response)
    {
        log.debug("New SOAP query. Getting started");
        long startTime = System.currentTimeMillis();

        String inputData =  request.getParameter("xml");
        String templateName =  request.getParameter("templateName");

        if (inputData == null || inputData.isEmpty() ){
            log.error("Error empty input paramenter xml");
            return;
        }

        if (templateName == null || templateName.isEmpty()){
            log.error("Error empty input paramenter templateName");
            return;
        }

        inputData = inputData.replace(".", "+");
        inputData = inputData.replace(",","/");
        inputData = inputData.replace("-", "=");

        //Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedByteArray = NewBase64.decode(inputData);
        String dataSourceXML = new String(decodedByteArray, StandardCharsets.UTF_8);

        String format = "pdf";
        String recvMessage = "";

        try {
            String soapMessage = SOAPHandler.GetSoapMessage(dataSourceXML, templateName, format);
            recvMessage = SOAPHandler.SendAndRecvSoap(soapMessage);
        } catch(Exception e){
            log.error("Error while send SOAP response");
            return;
        }

        byte[] file = NewBase64.decode(recvMessage);

        response.reset();
        response.setContentType("application/pdf");
        try {
            response.getOutputStream().write(file);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("Error while send SOAP responce to client");
        }

        long endTime = System.currentTimeMillis();
        log.info("New SOAP query. Send response to client. Time: " + (endTime - startTime));
    }



    @RequestMapping(value = "XPrGen", method = RequestMethod.GET)
    public @ResponseBody
    String XPrGen(HttpServletRequest request) {
        log.debug("New SOAP query. Getting started");
        long startTime = System.currentTimeMillis();


        String inputData =  request.getParameter("xml");
        String templateName =  request.getParameter("templateName");
        if (inputData == null || inputData.isEmpty() ){
            log.error("Error empty input paramenter xml");
            return "No input parameter: xml";
        }

        if (templateName == null || templateName.isEmpty()){
            log.error("Error empty input paramenter templateName");
            return "No input parameter: templateName";
        }

        inputData = inputData.replace(".","+");
        inputData = inputData.replace(",","/");
        inputData = inputData.replace("-","=");

        //Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedByteArray = NewBase64.decode(inputData);
        String dataSourceXML = new String(decodedByteArray, StandardCharsets.UTF_8);

        String format = "pdf";
        String recvMessage = "";

        try {
            String soapMessage = SOAPHandler.GetSoapMessage(dataSourceXML, templateName, format);
            recvMessage = SOAPHandler.SendAndRecvSoap(soapMessage);
        } catch(Exception e){
            log.error("Error while send SOAP response");
            return e.getMessage();
        }

        String ans = "<html>\n" +
                "<body>\n" +
                "\t<div align=\"center\">\n" +
                "\t\t<object>\n" +
                "\t\t<embed src=\"data:application/pdf;base64,";

        ans+= recvMessage;

        ans += "\" width=\"100%\" height=\"100%\" />\n" +
                "\t\t</div>\n" +
                "\t</<object>\n" +
                "</body>\n" +
                "</html>";

        long endTime = System.currentTimeMillis();
        log.debug("New SOAP query. Computing done. Time: " + (endTime - startTime));

        return ans;
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public String delete(HttpServletRequest request) {
        RegionInfoUtility.removeRecord(request.getParameter("id_in_db"));
        return "redirect:/region_edit";
    }

    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update(HttpServletRequest request) {
        RegionInfoUtility.updateRecord(request.getParameter("id_in_db"),
                new RegionInfo(request.getParameter("id_in_db"),
                        request.getParameter("id"),
                        request.getParameter("region_eng"),
                        request.getParameter("region_rus"),
                        request.getParameter("macro_region_eng"),
                        request.getParameter("macro_region_rus"),
                        request.getParameter("priority"),
                        request.getParameter("email"),
                        request.getParameter("logo_id"),
                        request.getParameter("status"),
                        request.getParameter("phone_b2c"),
                        request.getParameter("phone_b2b"),
                        request.getParameter("delivery_group_status"),
                        null));
        return "redirect:/region_edit";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) {
        RegionInfoUtility.addRecord(new RegionInfo(
                request.getParameter("id"),
                request.getParameter("region_eng"),
                request.getParameter("region_rus"),
                request.getParameter("macro_region_eng"),
                request.getParameter("macro_region_rus"),
                request.getParameter("priority"),
                request.getParameter("email"),
                request.getParameter("logo_id"),
                request.getParameter("status"),
                request.getParameter("phone_b2c"),
                request.getParameter("phone_b2b"),
                request.getParameter("delivery_group_status"),
                null));
        return "redirect:/region_edit";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String displaySortedMembers(Model model) {
        model.addAttribute("newMember", new Member());
        model.addAttribute("members", memberDao.findAllOrderedByName());
        return "index";
    }

    @RequestMapping(value = "DOC", method = RequestMethod.GET)
    public String clear() {
        return "DOC";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String registerNewMember(@Valid @ModelAttribute("newMember") Member newMember, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                memberDao.register(newMember);
                return "redirect:/";
            } catch (UnexpectedRollbackException e) {
                model.addAttribute("members", memberDao.findAllOrderedByName());
                // Check the uniqueness of the email address
                if (emailAlreadyExists(newMember.getEmail())) {
                    model.addAttribute("error", "Unique Email Violation");
                } else {
                    model.addAttribute("error", e.getCause().getCause());
                }
                return "index";
            }
        } else {
            model.addAttribute("members", memberDao.findAllOrderedByName());
            return "index";
        }
    }

    /**
     * Checks if a member with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    public boolean emailAlreadyExists(String email) {
        Member member = null;
        try {
            member = memberDao.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }
        return member != null;
    }

}
