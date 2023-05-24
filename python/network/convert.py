## Anna Sheaffer PPIN Web ##
## Started June 24, 2022  ##

import sys
import json

## python code that will be used to convert ensembl file to json ##
def main():
    # content based on the id data for worm and yeast
    w_content = read_data("worm_protein_ids106.txt")
    y_content = read_data("yeast_protein_ids_ensembl106.txt")

    # read the json file
    json = read_json("C:/users/annsb/OneDrive/Documents/PPIN_web/json/a_demo.json")

    # extract the desired data from the json file
    w_dict = extract_data(w_content)
    y_dict = extract_data(y_content)

    # combine the 2 dictionaries to make a master dictionary of all possible data
    master_dict = {**w_dict, **y_dict}

    update_json(json, master_dict, "C:/users/annsb/OneDrive/Documents/PPIN_web/json/a_demo_update.json")

# takes in a file name as param and reads the ensembl+
def read_data(file_name):
    with open(file_name) as f:
        contents = f.readlines()

    for i in range(len(contents)):
        contents[i] = contents[i].split('\t')

    return contents

# takes in a file path str as param and reads a current json file
def read_json(file_path):
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

# takes the data from ensembl file and makes it into a dictionary
def extract_data(content):
    gene_dict = {}

    # loop through the content
    for line in content:
        # temporarily set 'straightforward' data to the corresponding var
        gene_name = line[len(line)-1].strip()

        ncbi = line[1]
        ensembl = line[0]

        s_prot = line[2]
        t_prot = ""

        # check if there are both trembl and swiss prot ids
        if len(line) == 5:
            t_prot = line[3]

        # add the gene info to the dictionary
        gene_dict[gene_name] = [ensembl, ncbi, s_prot, t_prot]

    return gene_dict

# match a name from the json file with the corresponding id info from the ensembl file
# returns the list of ids associated with the protein name
def match_name(name, dict):
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

# take in the json file info and the dictionary with all of the protein data
# loop through the json file and add the dictionary data when necessary
def update_json(json, dict, filename):
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