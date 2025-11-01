import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import Layout from "../components/Layout";

export default function EditEtudiant() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [etudiant, setEtudiant] = useState({ code: "", nom: "", prenom: "" });
    const [loading, setLoading] = useState(true); // Etat de chargement

    useEffect(() => {
        axios.get(`http://localhost:8080/api/etudiants/${id}`)
            .then(res => {
                setEtudiant(res.data);
                setLoading(false); // Fin du chargement
            })
            .catch(err => {
                setLoading(false); // Fin du chargement en cas d'erreur
                // Pas de message d'erreur, on ne fait rien
            });
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEtudiant(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put(`http://localhost:8080/api/etudiants/${id}`, etudiant);
            alert("Étudiant modifié avec succès !");
            navigate("/etudiants");
        } catch (err) {
            console.error(err);
            alert("Erreur lors de la mise à jour.");
        }
    };

    // Si l'étudiant est en cours de chargement, afficher un message
    if (loading) {
        return (
            <Layout>
                <h1 className="text-2xl font-bold mb-6">Chargement...</h1>
            </Layout>
        );
    }

    return (
        <Layout>
            <h1 className="text-2xl font-bold mb-6">Modifier Étudiant</h1>
            <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow w-full max-w-md mx-auto">
                <div className="mb-4">
                    <label className="block text-sm font-medium mb-1">Code</label>
                    <input
                        type="text"
                        name="code"
                        value={etudiant.code}
                        onChange={handleChange}
                        className="w-full p-2 border border-gray-300 rounded"
                        required
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-sm font-medium mb-1">Nom</label>
                    <input
                        type="text"
                        name="nom"
                        value={etudiant.nom}
                        onChange={handleChange}
                        className="w-full p-2 border border-gray-300 rounded"
                        required
                    />
                </div>
                <div className="mb-6">
                    <label className="block text-sm font-medium mb-1">Prénom</label>
                    <input
                        type="text"
                        name="prenom"
                        value={etudiant.prenom}
                        onChange={handleChange}
                        className="w-full p-2 border border-gray-300 rounded"
                        required
                    />
                </div>
                <button
                    type="submit"
                    className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                >
                    Enregistrer
                </button>
            </form>
        </Layout>
    );
}
