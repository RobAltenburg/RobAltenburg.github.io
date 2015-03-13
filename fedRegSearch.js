/*
 * fedRegSearch.js
 * https://github.com/qrpc/FedRegComments
 *
 * Copyright 2013, Robert Altenburg
 *
 * Licensed under the GPLv3 license or any later version:
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

var fedRegSearch = {
baseURL:  'https://www.federalregister.gov/api/v1/articles.json?per_page=500&order=relevance&fields%5B%5D=action&fields%5B%5D=agency_names&fields%5B%5D=dates&fields%5B%5D=docket_id&fields%5B%5D=publication_date&fields%5B%5D=title&fields%5B%5D=topics&fields%5B%5D=type&fields%5B%5D=comments_close_on&fields%5B%5D=html_url&conditions%5Bagencies%5D%5B%5D=environmental-protection-agency&conditions%5Bagencies%5D%5B%5D=nuclear-regulatory-commission&conditions%5Bagencies%5D%5B%5D=mine-safety-and-health-administration&conditions%5Bagencies%5D%5B%5D=federal-energy-regulatory-commission&conditions%5Bagencies%5D%5B%5D=engineers-corps&conditions%5Bagencies%5D%5B%5D=surface-mining-reclamation-and-enforcement-office&conditions%5Bagencies%5D%5B%5D=energy-department',
    
    dateFormat: function(inDate) {
        var dd = inDate.getDate();
        var mm = inDate.getMonth() + 1;
        var yyyy = inDate.getFullYear();
        return mm + '-' + dd + '-' + yyyy;
    },
    
    fedDateFormat: function(inDate) {
        var dd = ('0' + inDate.getDate()).slice(-2);
        var mm = ('0' + (inDate.getMonth() + 1)).slice(-2);
        return inDate.getFullYear() + '-' + mm + '-' + dd;
    },
    
    ajaxGetTable: function(searchDate, tableTag) {
        
        // set a message when the content is loading
        $(tableTag).html('Loading, please wait...'); 
                    
        $.ajax({
            url: this.baseURL + '&conditions%5Bpublication_date%5D%5Bgte%5D=' + this.fedDateFormat(searchDate),
            dataType: 'jsonp',
            async:false,
            success: function(data) {

                var agencies ={'Environmental Protection Agency':'EPA', 'Nuclear Regulatory Commission':'NRC','Mine Safety and Health Administration':'MSHA','Federal Energy Regulatory Commission':'FERC','Engineers Corps':'Army Corps','Surface Mining Reclamation and Enforcement Office':'OSM','Energy Department':'DOE'};

                // build the output table
                var output = "<table class='frstyle' id='fedTable'>\n"
                    output += "<thead><tr><th class='t_title'>Title</th><th>Action</th><th>Agency</th><th>Docket ID</th><th class='t_dates'>"
                    output += "Comments Close</th><th class='t_dates'>Publication Date</th></tr></thead><tbody>\n"

                for (var i in data.results) {
                    if (data.results[i].comments_close_on != null) {
                        var my_agency = data.results[i].agency_names[data.results[i].agency_names.length - 1]
                        output += "<tr><td>" + data.results[i].title+"</td>";
                        output += "<td>" + data.results[i].action+"</td>";
                        output += "<td>" + agencies[my_agency] +"</td>";
                        output += "<td><a href = '" +  data.results[i].html_url + "'>"; 
                        if (data.results[i].docket_id != null) {
                            output += data.results[i].docket_id+"</a></td>";
                            } else {
                            output += "No I.D.</a></td>";
                            }
                        output += "<td>" + data.results[i].comments_close_on+"</td>";
                        output += "<td>" + data.results[i].publication_date+"</td></tr>\n";    
                    }}
                output += "</tbody></table>\n";
                $(tableTag).html(output);          
                $('#fedTable').dataTable({
                    "bPaginate": false,
                    "aaSorting": [[ 5, "asc" ]]
                     } );                 
            },  // end success 
            error: function(data) {
                $(tableTag).html("An error occurred loading the requested data");
                
            }    // end error    
        }); // end ajax call    
    }
};
