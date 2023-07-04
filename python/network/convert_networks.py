"""
    convert_networks.py: converts ensembl/Biogrid files to a json format
"""
__author__ = "Anna Sheaffer"
__email__ = "asheaffe@iwu.edu"
__date__ = "June 24, 2022"

import sys
import json

def main():
    network_yeast = '../../../PPI-Network-Alignment/networks/s_cerevisiae.network-109-4.4.219.txt'  # species 1
    network_worm = '../../../PPI-Network-Alignment/networks/c_elegans.network-109-4.4.219.txt'      # species 2

    # access ensembl data from PPI-Network-Alignment repo
    ensembl_ncbi_yeast = "../../../PPI-Network-Alignment/ensembl/s_cerevisiae_ensembl_ncbi-109.txt"
    ensembl_others_yeast = "../../../PPI-Network-Alignment/ensembl/s_cerevisiae_ensembl_others-109.txt"
    ensembl_ncbi_worm = "../../../PPI-Network-Alignment/ensembl/c_elegans_ensembl_others-109.txt"
    ensembl_others_worm = "../../../PPI-Network-Alignment/ensembl/c_elegans_ensembl_others-109.txt"

    # assemble ensembl data from each species
    data_yeast = extract_ensembl_data_ncbi(ensembl_ncbi_yeast)
    ensembl_data_yeast = extract_ensembl_data_other_ids(ensembl_others_yeast, data_yeast)

    #data_worm = extract_ensembl_data_ncbi(ensembl_ncbi_worm)
    #ensembl_data_worm = extract_ensembl_data_other_ids(ensembl_others_worm, data_worm)

    # assemble network data from each species
    #network_data_yeast = read_network_file(network_yeast)
    #network_dict_yeast = list_to_dict(network_data_yeast)

    #network_data_worm = read_network_file(network_worm)
    #network_dict_worm = list_to_dict(network_data_worm)

    #query_subnetwork(network_dict_yeast, 'YJL003W', network_dict_worm, 'WBGene00000103', ensembl_data_yeast, ensembl_data_worm, "S cerevisiae", "C elegans")

    with open("test_network_data.txt", "w") as f1:
        f1.write("! Test file: Taking S cerevisiae interaction data and making it into a dict\n" +
                 "! Currently showing all interactions for each protein\n")
        for entry in ensembl_data_yeast:
            line = ensembl_data_yeast[entry]
            f1.write(str(entry) + "\t" + str(line) + "\n")

def query_subnetwork(p_dict_s1, prot_s1, p_dict_s2, prot_s2, protList_s1, protList_s2, s1, s2):
    """
    Takes in a query protein and query species and returns a subnetwork in JSON format

    :param p_dict_s1: dictionary of proteins and their connections as def list for species 1
    :param prot_s1: desired protein as str for species 1
    :param p_dict_s2: dictionary of proteins and their connections as def list for species 2
    :param prot_s2: desired protein as str for species 2
    :param protList_s1: list of proteins as dict (from extract_ensembl functions)
    :param protList_s2: list of proteins as dict (from extract_ensembl functions)
    :param s1: str species 1 name
    :param s2: str species 2 name
    :return: list of dict values that corresponds with the desired subnetwork
    """
    prot_def1 = p_dict_s1[prot_s1]
    prot_def2 = p_dict_s2[prot_s2]

    # add the query protein to the list of its interacting proteins
    temp1 = prot_def1
    temp1.append(prot_s1)

    temp2 = prot_def2
    temp2.append(prot_s2)

    retList1 = list_to_nodes(temp1, protList_s1, 1)
    nodes1 = retList1[0]  # protein nodes to become JSON
    edge_dict1 = retList1[1]  # dict of protein name as key and corresponding id as def

    retList2 = list_to_nodes(temp2, protList_s2, 2)
    nodes2 = retList2[0]  # protein nodes to become JSON
    edge_dict2 = retList2[1]  # dict of protein name as key and corresponding id as def

    edges1 = list_to_edges(prot_s1, prot_def1, edge_dict1)
    edges2 = list_to_edges(prot_s2, prot_def2, edge_dict2)

    json_header = [
                  {"data":
                       {"id": "species1", "name": s1},
                        "_comment": "Test output for a JSON file -- contains a subnetwork of C. elegans and a subnetwork of M. musculus",
                        "classes": "container s1"},
                  {"data": {"id": "species2", "name": s2}, "classes": "container s2"},
                  {"data": {"id": "aligned non-ortho", "name": "aligned non-edges"}, "classes": "container"},
                  {"data": {"id": "aligned ortho", "name": "aligned edges"}, "classes": "container"}]

    # add all lists of dict objects together to form the whole JSON file
    json_header.extend(nodes1)
    json_header.extend(edges1)
    json_header.extend(nodes2)
    json_header.extend(edges2)
    json_str = json.dumps(json_header)

    file = open("test_json.json", "w")
    file.write(json_str)

def list_to_nodes(p_inters, ensembl_data, species_num):
    """
    Converts python dictionary to JSON format with established conventions.
    Writes to a JSON file, returns nothing.

    :param p_inters: dictionary of proteins and their connections as def list
    :param ensembl_data: dict of alternate ids from ensembl file
    :param species_num: species 1 or 2?
    :return: final list of JSON elements without edges, dict of edge ids and e_ids
    """
    # take a dictionary with the entrezgene as the key
    #d = {x.get_p_sid(): x for x in ensembl_data}

    # the list that will be ultimately converted to JSON
    # start with comment and protein category boxes
    final_list = []

    # dictionary that will hold node ids and edges
    edge_dict = {}

    count = 0
    for key in p_inters:
        json_dict = {"data": {}}
        json_dict["data"]["id"] = str(species_num) + "." + str(count)
        for i in range(6):
            if not ensembl_data[key][i] or '' in ensembl_data[key][i]:
                ensembl_data[key][i] = {}

        json_dict["data"]["e_id"] = key
        json_dict["data"]["t_id"] = ensembl_data[key][0]
        json_dict["data"]["p_id"] = ensembl_data[key][1]
        json_dict["data"]["name"] = ensembl_data[key][2]
        json_dict["data"]["ncbi"] = ensembl_data[key][3]
        json_dict["data"]["swissprot"] = ensembl_data[key][4]
        json_dict["data"]["trembl"] = ensembl_data[key][5]
        json_dict["data"]["refseq"] = ensembl_data[key][6]
        json_dict["data"]["parent"] = "unaligned"

        json_dict["classes"] = "species" + str(species_num) + " unaligned protein"

        # add data to edge dictionary
        edge_dict[key] = json_dict["data"]["id"]

        final_list.append(json_dict)

        count += 1

    # the last entry in the dict will be the query
    json_dict["classes"] = json_dict["classes"] + " query"

    return [final_list, edge_dict]

def list_to_edges(prot, e_list, e_dict):
    """
    Takes the converted nodes and writes edges to JSON based on established conventions
    and data in the established python dictionary

    :param prot: dictionary of each protein in the network and their connections
    :param e_list: list of edges within the network?
    :param edge_dict: dictionary of id and its corresponding ensembl id
    :return: list of JSON edges
    """

    # final list of JSON edges to be returned
    final_list = []

    temp = {}

    # loop through p_dict
    for ele in e_list:
        temp = {"data": {}}

        # set the source to the current protein
        temp["data"]["source"] = e_dict[prot]

        # set the target to the interacting protein
        temp["data"]["target"] = e_dict[ele]

        temp["classes"] = "species" + str(e_dict[prot][0]) + " edge"

        #print(temp)

        # add to the final list
        final_list.append(temp)

    return final_list

def list_to_dict(map_list):
    """
    Constructs a dictionary with a list of each protein it interacts with
    as def

    :param map_list: list of mapped protein ids
    :return: dict of each protein id and it's interactors
    """

    prot_hash = {}
    for interaction in map_list:
        current = 0
        for prot in interaction:
            # check if the protein already exists in the dictionary
            if prot not in prot_hash:
                prot_hash[prot] = []    # create a new list if not in dict

            prot2 = interaction[current - 1]    # receive interacting protein (either 0 or -1)

            # check if the interacting protein is already in the current def list
            # and also not in the dict already so that there aren't duplicate interactions
            if prot2 not in prot_hash[prot]:
                # add the interacting protein to the list of interactors for the current
                prot_hash[prot].append(interaction[current - 1])

            current += 1

    return prot_hash

def read_network_file(filename):
    """
    Takes in a filepath for a network file from PPI-Network-Alignment and stores data as a dict
    :param filename: filepath as str
    :return: list of tuple interaction pairs
    """
    network_data = []
    with open(filename, "r") as f:
        for line in f:
            if line[0] != "!" and line[0] != "\n":
                interaction = line.strip().split('\t')
                network_data.append(tuple(interaction))

    return network_data

def read_data(file_name):
    """
        read_data(): takes in a file name as param and reads the ensembl file

        :param file_name: filename as str
        :return: contents of the file as list
    """
    contents = []
    with open(file_name) as f:
        f.readline()
        for line in f:
            contents.append(line.strip().split('\t'))

    return contents

def read_json(file_path):
    """
    Takes in a file path as param and reads a current json file
    :param file_path: str file path
    :return: json file contents as dict
    """
    with open(file_path) as file:
        data = json.load(file)

    for i in range(len(data)):
        # do not consider container nodes
        if data[i]['classes'] == 'container':
            break

        if 'id' in data[i]['data']:
            current = data[i]['data']['name']
            current = current.split(",")

            for j in range(len(current)):
                current[j] = current[j].strip()

            data[i]['data']['name'] = current

        #print(data)
        #print(data[1])
        #print(data[1]['data'])
        #print(data[1]['data']['name'])
        #print(data[4]['data']['name'].split(','))

    return data

def extract_ensembl_data_ncbi(filename):
    """
    Takes the data from the ensembl file with the ncbi id and initializes the dictionary with ids
    with the gene stable id as the key
    :param content: ensembl data as list
    :return: dict ensembl data
    """
    with open(filename, 'r') as content:
        content.readline()
        gene_dict = {}

        # loop through the content
        for line in content:
          line = line.strip().split('\t')

          while len(line) < 8:
              line.append("")

          if line[0] in gene_dict:
              for i in range(1, len(line)-1):
                  if gene_dict[line[0]][i] != "":
                    if len(gene_dict[line[0]][i]) == 1:
                        gene_dict[line[0]][i][1] = line[i]
                    else:
                        gene_dict[line[0]][i][len(gene_dict[line[0]][i])+1] = line[i]

          else:
              # key is the gene stable id because every line in the file will have one ('primary key')
              gene_dict[line[0]] = []

              for ele in line[1:]:
                gene_dict[line[0]].append({1:ele})

    return gene_dict

def extract_ensembl_data_other_ids(filename, ensembl_dict):
    """
    Takes the data from the ensembl file with the other ids (swissprot, trembl, refseq)
    and appends to the list of other ids if it already exists in the dict, otherwise adds a new
    key/def for any new id entries
    :param filename: filepath for the other id file
    :param ensembl_dict: dict with ensembl data that has already been created
    :return: updated dict with ensembl data
    """
    with open(filename, 'r') as content:
      content.readline()

      for line in content:
        line = line.strip().split('\t')

        while len(line) < 8:
            line.append("")

        for i in range(4, len(line)-1):
            if ensembl_dict[line[0]][i] != "":
                    if len(ensembl_dict[line[0]][i]) == 1:
                        ensembl_dict[line[0]][i][1] = line[i]
                    else:
                        ensembl_dict[line[0]][i][len(ensembl_dict[line[0]][i])+1] = line[i]

    return ensembl_dict

def match_name(name, dict):
    """
    Matches the name from a json file with the corresponding id info from the ensembl file
    Returns the list of ids associated with the protein name
    :param name: Key from json dict
    :param dict: ensembl dictionary
    :return: list of ids associated with the given protein name
    """
    # list of names to be returned
    id_list = []

    if name.lower() in dict:
        id_list = dict[name.lower()]
    elif name.upper() in dict:
        id_list = dict[name.upper()]
    else:
        id_list = None

    return id_list

'''
AAA-1, 848483, Q93783, ENSG3883839393
www.ncbi.nih.gov/gene/848483
www.uniprot.com/Q37383
www.ensemble.org/?q=ENSG108839393939

'''

def update_json(json, dict, filename):
    """
    Take in the json file info and dict with protein data and write data to a given file
    Loop through json file and add the dict data when necessary
    :param json: json dict
    :param dict: ensembl dict
    :param filename: name of file to be written to
    :return: None
    """
    temp = []

    original_stdout = sys.stdout
    with open(filename, 'w') as f:
        sys.stdout = f

        print("[")

        # loop through the json file
        for i in range(len(json)):
            # check if the data has element "name"
            if 'name' in json[i]['data']:
                name_list = json[i]['data']['name']
            else:
                print(str(json[i]).replace("'", '"'), ",", sep="")
                continue # might want a diff approach????

                # name_list remains the same after the name does not change
                # i.e. the line is an edge and therefore does not have a name
                # will continue to print the info for the last node along with the
                # edge print statement

            # pull the name from the line
            for name in name_list:
                is_matched = match_name(name, dict)
                if is_matched is not None:
                    temp = temp + match_name(name, dict)

            # boolean value that keeps track of whether there are
            # multiple proteins to a node
            mult = False
            if temp != []:
                if len(name_list) > 1:
                    mult = True

                print('{ "data":', end='')
                if 'id' in json[i]['data']:
                    print(' { "id": "' + json[i]['data']['id'] + '",', end='')

                print(' "e_id": "' + temp[0] + '",', end='')
                if mult:
                    print(' "e_id2": "' + temp[4] + '",', end='')
                print(' "name": "' + ", ".join(name_list) + '",', end='')

                # not all elements will have a parent
                if 'parent' in json[i]['data']:
                    print(' "parent": "' + json[i]['data']['parent'] + '",', end='')

                print(' "ncbi": "' + temp[1] + '",', end='')
                if mult:
                    print(' "ncbi2": "' + temp[5] + '",', end='')

                if temp[2] != "":
                    print(' "uniprot": "' + temp[2] + '"', end='')
                elif temp[3] != "":
                    print(' "uniprot": "' + temp[3] + '"', end='')

                if mult:
                    if temp[6] != "":
                        print(', "uniprot2": "' + temp[6] + '"', end='')
                    elif temp[7] != "":
                        print(', "uniprot2": "' + temp[7] + '"', end='')
                print("}", end='')

                if "classes" in json[i]:
                    print(', "classes": "' + json[i]['classes'] + '"}', end='')

            else:
                print(str(json[i]).replace("'", '"'), end='')

            # only have a comma if the current line is not the last one
            if i != len(json)-1:
                print(",")

            temp = []

        print("]")

        sys.stdout = original_stdout

main()