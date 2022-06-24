## Anna Sheaffer PPIN Web ##
## Started June 24, 2022  ##

## python code that will be used to convert ensembl file to json ##
def main():
    content = read_file("human_protein_ids_ensembl106.txt")
    names = get_gene_name(content)
    print(names)

# takes in a file name as param and reads the file
def read_file(file_name):
    with open(file_name) as f:
        contents = f.readlines()

    return contents

# takes the contents of a file as str and returns the gene name
def get_gene_name(contents):
    # create an empty list
    names = []

    # loop through the contents passed by param
    for line in contents[1:]:
        temp = line.split("\t")

        names.append(temp[0])

    return names
main()