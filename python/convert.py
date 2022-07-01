## Anna Sheaffer PPIN Web ##
## Started June 24, 2022  ##

import sys
import json

## python code that will be used to convert ensembl file to json ##
def main():
    content = read_data("worm_protein_ids106.txt")
    json = read_json("C:/users/annsb/OneDrive/Documents/PPIN_web/json/a_demo.json")
    e_dict = extract_data(content)
    p_list = match_name(json, e_dict)
    print(p_list)
    #update_json(p_list, e_dict, json)

# takes in a file name as param and reads the ensembl
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
        if 'id' in data[i]['data']:
            current = data[i]['data']['name']
            current = current.split(",")

            for j in range(len(current)):
                current[j] = current[j].strip()

            data[i]['data']['name'] = current
        else:
            data[i]['data']['name'] = []

        #print(data)
        #print(data[1])
        #print(data[1]['data'])
        #print(data[1]['data']['name'])
        #print(data[4]['data']['name'].split(','))

    return data

# takes the data from the content and makes it into a dictionary
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

# find the name from each element in the json file and match it with a name in the ensemble file
def match_name(json, dict):
    # list of names to be returned
    name_list = []

    # loop through all of the proteins in the json file
    for el in json:
        for name in el['data']['name']:
            if name.lower() in dict: # other special cases??????
                name_list.append(name.lower())
            elif name.upper() in dict:
                name_list.append(name.upper())

    return name_list


'''
AAA-1, 848483, Q93783, ENSG3883839393
www.ncbi.nih.gov/gene/848483
www.uniprot.com/Q37383
www.ensemble.org/?q=ENSG108839393939

'''

# use the name list to update the json file based on the name list generated in match_name
def update_json(name_list, dict, json):
    # loop through the json file
    for i in range(len(json)):
        for el in json[i]["data"]:
            # check if the data has reached "name"
            if el == "name":
                # join the list of names
                json[i]["data"][el] = ", ".join(json[i]["data"][el])

                print(el, ": ", json[i]["data"][el])

            # otherwise, just print the data as usual
            else:
                print(el, ": ", json[i]["data"][el])


main()