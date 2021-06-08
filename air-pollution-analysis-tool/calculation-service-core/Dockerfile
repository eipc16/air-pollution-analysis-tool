FROM continuumio/miniconda3
MAINTAINER eipc16

WORKDIR /app

COPY . .

RUN conda update conda
RUN --mount=type=cache,mode=0777,target=/opt/conda/pkgs conda env create -f env.yml

SHELL ["conda", "run", "-n", "calc-service", "/bin/bash", "-c"]

RUN echo "Make sure quart is installed:"
RUN python -c "import quart"

ENTRYPOINT ["conda", "run", "--no-capture-output", "-n", "calc-service", "python", "app.py"]